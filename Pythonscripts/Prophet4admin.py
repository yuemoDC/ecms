import mysql.connector
import pandas as pd
import matplotlib.pyplot as plt
from prophet import Prophet
import os
import sys
import io

# 设置输出为 utf-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 读取命令行参数（只传入预测天数）
forecast_days = int(sys.argv[1])

# 构造 data 目录路径
base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "data"))
os.makedirs(base_dir, exist_ok=True)

# 生成文件名
filename = f'sales_forecast_all_merchants_{forecast_days}_days.csv'
file_path = os.path.join(base_dir, filename)

# 连接数据库
conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='Guy9nzh9ng123!',
    database='ecms',
    use_pure=True
)
cursor = conn.cursor()

# 查询所有商家每天的总销售额（按日期聚合）
query = """
    SELECT sales_date AS ds, SUM(total_sales) AS y
    FROM sales_data
    GROUP BY sales_date
    ORDER BY sales_date
"""
df = pd.read_sql(query, conn)
df['ds'] = pd.to_datetime(df['ds'])  # 确保是 datetime 类型

# 创建 Prophet 模型并拟合数据
model = Prophet(
    yearly_seasonality=True,
    weekly_seasonality=True,
    daily_seasonality=False
)
model.fit(df)

# 构造预测未来天数的数据
future = model.make_future_dataframe(periods=forecast_days)
forecast = model.predict(future)

# 保存预测结果到 CSV 文件
forecast_to_save = forecast[['ds', 'yhat', 'yhat_lower', 'yhat_upper', 'trend', 'weekly', 'yearly']]
forecast_to_save.to_csv(file_path, index=False)

# 创建 forecast_files 表并插入记录
cursor.execute("""
    CREATE TABLE IF NOT EXISTS forecast_files (
        id INT AUTO_INCREMENT PRIMARY KEY,
        merchant_id INT DEFAULT NULL,
        file_path VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""")
# 删除旧记录（merchant_id 为 NULL 的表示“全部商家”）
cursor.execute("DELETE FROM forecast_files WHERE merchant_id IS NULL")
cursor.execute("INSERT INTO forecast_files (merchant_id, file_path) VALUES (%s, %s)", (None, file_path))

# 提交事务并关闭连接
conn.commit()
cursor.close()
conn.close()

print(f"文件已保存为：{file_path}，并已记录到数据库。")
