import mysql.connector
import pandas as pd
import matplotlib.pyplot as plt
from prophet import Prophet
import os
import sys
import io

# 设置输出为 utf-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 读取命令行参数
merchant_id = int(sys.argv[1])
forecast_days = int(sys.argv[2])

# 构造项目内 data 目录的路径
base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "data"))
os.makedirs(base_dir, exist_ok=True)

filename = f'sales_forecast_merchant_{merchant_id}_{forecast_days}_days.csv'
file_path = os.path.join(base_dir, filename)

# 连接到数据库
conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='123456',
    database='ecms',
    use_pure=True
)
cursor = conn.cursor()

# 查询数据
query = f"""
    SELECT sales_date AS ds, total_sales AS y
    FROM sales_data
    WHERE merchant_id = {merchant_id}
"""
df = pd.read_sql(query, conn)
df['ds'] = pd.to_datetime(df['ds'])

# 创建 Prophet 模型
model = Prophet(
    yearly_seasonality=True,
    weekly_seasonality=True,
    daily_seasonality=False
)
model.fit(df)

# 预测未来指定天数
future = model.make_future_dataframe(periods=forecast_days)
forecast = model.predict(future)

# 保存预测结果
forecast_to_save = forecast[['ds', 'yhat', 'yhat_lower', 'yhat_upper', 'trend', 'weekly', 'yearly']]
forecast_to_save.to_csv(file_path, index=False)

# 写入文件路径到数据库
cursor.execute("""
    CREATE TABLE IF NOT EXISTS forecast_files (
        id INT AUTO_INCREMENT PRIMARY KEY,
        merchant_id INT NOT NULL,
        file_path VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""")
cursor.execute("DELETE FROM forecast_files WHERE merchant_id = %s", (merchant_id,))
cursor.execute("INSERT INTO forecast_files (merchant_id, file_path) VALUES (%s, %s)", (merchant_id, file_path))

conn.commit()
cursor.close()
conn.close()

print(f"文件已保存为：{file_path}，并已记录到数据库。")
