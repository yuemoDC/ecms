package com.ecms.service;

import org.springframework.stereotype.Service;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.MathArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ARIMAPredictionService {

    private static final Logger logger = LoggerFactory.getLogger(ARIMAPredictionService.class);

    /**
     * Uses ARIMA model to forecast future sales trends
     * @param historicalData historical sales data
     * @param p AR parameter
     * @param d differencing order
     * @param q MA parameter
     * @param forecastPeriods prediction periods
     * @return forecast results
     */
    public List<Double> forecastARIMA(List<Double> historicalData, int p, int d, int q, int forecastPeriods) {
        try {
            if (historicalData == null || historicalData.isEmpty()) {
                logger.warn("Historical data is empty, unable to perform ARIMA forecast");
                return generateRandomForecast(100, 200, forecastPeriods);
            }

            if (historicalData.size() < 5) {
                logger.warn("Too few historical data points ({}), using simple forecasting instead", historicalData.size());
                return generateRandomForecast(
                        historicalData.get(historicalData.size()-1) * 0.8,
                        historicalData.get(historicalData.size()-1) * 1.2,
                        forecastPeriods
                );
            }

            // Apply differencing
            List<Double> diffData = historicalData;
            for (int i = 0; i < d; i++) {
                diffData = difference(diffData);
                if (diffData.isEmpty()) {
                    logger.warn("Differenced data is empty, falling back to simple forecast");
                    return generateRandomForecast(
                            historicalData.get(historicalData.size()-1) * 0.8,
                            historicalData.get(historicalData.size()-1) * 1.2,
                            forecastPeriods
                    );
                }
            }

            // Estimate AR and MA parameters
            double[] arParams = estimateARParams(diffData, p);
            double[] maParams = estimateMAParams(diffData, q);

            // Perform forecast
            List<Double> forecastedDiff = forecast(diffData, arParams, maParams, forecastPeriods);

            // Inverse differencing to convert forecast back to original scale
            List<Double> forecastedValues = inverseDifference(forecastedDiff, historicalData, d);

            // Ensure forecast values are reasonable
            for (int i = 0; i < forecastedValues.size(); i++) {
                if (Double.isNaN(forecastedValues.get(i)) || Double.isInfinite(forecastedValues.get(i))) {
                    logger.warn("Unreasonable value in forecast, replacing with last known reasonable value");
                    forecastedValues.set(i, historicalData.get(historicalData.size() - 1));
                }

                // Ensure values are positive and within reasonable range
                forecastedValues.set(i,
                        Math.max(0.1, Math.min(forecastedValues.get(i),
                                historicalData.stream().mapToDouble(v -> v).max().orElse(1000) * 2)));
            }

            return forecastedValues;
        } catch (Exception e) {
            logger.error("Error during ARIMA forecasting", e);
            return generateRandomForecast(
                    historicalData.isEmpty() ? 100 : historicalData.get(historicalData.size()-1) * 0.8,
                    historicalData.isEmpty() ? 200 : historicalData.get(historicalData.size()-1) * 1.2,
                    forecastPeriods
            );
        }
    }

    /**
     * Generate random forecast (as fallback)
     */
    private List<Double> generateRandomForecast(double min, double max, int periods) {
        List<Double> forecast = new ArrayList<>();
        double value = min + Math.random() * (max - min);

        for (int i = 0; i < periods; i++) {
            // Add some random fluctuation
            double change = (Math.random() - 0.5) * (max - min) * 0.1;
            value = value + change;
            value = Math.max(min, Math.min(max, value)); // Keep within range
            forecast.add(value);
        }

        return forecast;
    }

    /**
     * Differencing operation
     * @param data input data
     * @return differenced data
     */
    private List<Double> difference(List<Double> data) {
        if (data.size() < 2) {
            return Collections.emptyList();
        }

        List<Double> differenced = new ArrayList<>();

        // Skip first element as it doesn't have a previous value to difference with
        for (int i = 1; i < data.size(); i++) {
            differenced.add(data.get(i) - data.get(i - 1));
        }

        return differenced;
    }

    /**
     * Inverse differencing operation
     * @param diffData differenced data
     * @param originalData original data
     * @param d differencing order
     * @return undifferenced data
     */
    private List<Double> inverseDifference(List<Double> diffData, List<Double> originalData, int d) {
        if (d == 0 || originalData.isEmpty()) {
            return diffData;
        }

        List<Double> result = new ArrayList<>(diffData);

        // Use last value of original data as starting point
        double lastValue = originalData.get(originalData.size() - 1);

        // Perform inverse differencing
        for (int i = 0; i < result.size(); i++) {
            lastValue = lastValue + result.get(i);
            result.set(i, lastValue);
        }

        if (d > 1) {
            // For multi-level differencing, recursively apply inverse differencing
            return inverseDifference(result, originalData, d - 1);
        }

        return result;
    }

    /**
     * Estimate AR parameters
     * @param data differenced data
     * @param p AR parameter order
     * @return AR parameters
     */
    private double[] estimateARParams(List<Double> data, int p) {
        if (p == 0 || data.size() <= p) {
            return new double[0];
        }

        try {
            // Use Yule-Walker equations to estimate AR parameters
            double[] acf = computeAutocorrelation(data, p);
            RealMatrix r = new Array2DRowRealMatrix(p, p);
            RealVector b = new ArrayRealVector(p);

            for (int i = 0; i < p; i++) {
                b.setEntry(i, acf[i + 1]);
                for (int j = 0; j < p; j++) {
                    r.setEntry(i, j, acf[Math.abs(i - j)]);
                }
            }

            DecompositionSolver solver = new LUDecomposition(r).getSolver();
            RealVector solution = solver.solve(b);

            double[] arParams = new double[p];
            for (int i = 0; i < p; i++) {
                arParams[i] = solution.getEntry(i);
                // Ensure parameters are stable
                if (Double.isNaN(arParams[i]) || Double.isInfinite(arParams[i])) {
                    return defaultARParams(p);
                }
            }
            return arParams;
        } catch (Exception e) {
            logger.warn("AR parameter estimation failed, using default parameters", e);
            // If solving fails, return simple estimate
            return defaultARParams(p);
        }
    }

    /**
     * Estimate MA parameters
     * @param data differenced data
     * @param q MA parameter order
     * @return MA parameters
     */
    private double[] estimateMAParams(List<Double> data, int q) {
        if (q == 0 || data.size() <= q) {
            return new double[0];
        }

        try {
            // Simplified implementation: use residual autocorrelation to estimate MA parameters
            double[] residuals = computeResiduals(data);
            double[] acf = computeAutocorrelation(Arrays.stream(residuals).boxed().toList(), q);

            double[] maParams = new double[q];
            for (int i = 0; i < q; i++) {
                maParams[i] = acf[i + 1];
                // Ensure parameters are stable
                if (Double.isNaN(maParams[i]) || Double.isInfinite(maParams[i])) {
                    maParams[i] = 0.1 / (i + 1); // Use simple value
                }
            }

            return maParams;
        } catch (Exception e) {
            logger.warn("MA parameter estimation failed, using default values", e);
            // Default MA parameters
            double[] defaults = new double[q];
            for (int i = 0; i < q; i++) {
                defaults[i] = 0.1 / (i + 1);
            }
            return defaults;
        }
    }

    /**
     * Compute residuals
     * @param data data
     * @return residuals array
     */
    private double[] computeResiduals(List<Double> data) {
        if (data.isEmpty()) return new double[0];

        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return data.stream().mapToDouble(val -> val - mean).toArray();
    }

    /**
     * Compute autocorrelation function
     * @param data data
     * @param maxLag maximum lag
     * @return autocorrelation array
     */
    private double[] computeAutocorrelation(List<Double> data, int maxLag) {
        if (data.isEmpty() || data.size() <= maxLag) {
            // Return default values
            double[] defaults = new double[maxLag + 1];
            defaults[0] = 1.0; // Zero lag autocorrelation is always 1
            for (int i = 1; i <= maxLag; i++) {
                defaults[i] = 1.0 / (i + 1); // Simple decay
            }
            return defaults;
        }

        double[] result = new double[maxLag + 1];
        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double[] centered = data.stream().mapToDouble(val -> val - mean).toArray();

        double denominator = 0;
        for (double v : centered) {
            denominator += v * v;
        }

        // Prevent division by zero
        if (denominator == 0) {
            result[0] = 1.0;
            for (int k = 1; k <= maxLag; k++) {
                result[k] = 0.0;
            }
            return result;
        }

        for (int k = 0; k <= maxLag; k++) {
            double numerator = 0;
            for (int t = 0; t < data.size() - k; t++) {
                numerator += centered[t] * centered[t + k];
            }
            result[k] = numerator / denominator;
        }

        return result;
    }

    /**
     * Default AR parameters (when normal estimation fails)
     * @param p AR order
     * @return default parameters
     */
    private double[] defaultARParams(int p) {
        double[] params = new double[p];
        double value = 0.5;
        for (int i = 0; i < p; i++) {
            params[i] = value / (i + 1);
        }
        return params;
    }

    /**
     * Forecast future values
     * @param data differenced data
     * @param arParams AR parameters
     * @param maParams MA parameters
     * @param periods forecast periods
     * @return forecast results
     */
    private List<Double> forecast(List<Double> data, double[] arParams, double[] maParams, int periods) {
        if (data.isEmpty()) {
            return generateRandomForecast(0, 10, periods);
        }

        List<Double> forecast = new ArrayList<>();
        int p = arParams.length;
        int q = maParams.length;

        // Calculate model residuals
        Queue<Double> residuals = new LinkedList<>();
        for (int i = 0; i < q; i++) {
            residuals.offer(0.0); // Initialize residuals to 0
        }

        // Initialize forecast with values from end of original data
        Queue<Double> history = new LinkedList<>();
        for (int i = Math.max(data.size() - p, 0); i < data.size(); i++) {
            history.offer(data.get(i));
        }

        // Ensure history queue contains at least p elements
        while (history.size() < p) {
            history.offer(0.0);
        }

        // Perform forecast
        for (int i = 0; i < periods; i++) {
            double prediction = 0.0;

            try {
                // Apply AR part
                List<Double> historyList = new ArrayList<>(history);
                for (int j = 0; j < p; j++) {
                    prediction += arParams[j] * historyList.get(historyList.size() - 1 - j);
                }

                // Apply MA part
                List<Double> residualsList = new ArrayList<>(residuals);
                for (int j = 0; j < q; j++) {
                    prediction += maParams[j] * residualsList.get(residualsList.size() - 1 - j);
                }

                // Check if prediction is reasonable
                if (Double.isNaN(prediction) || Double.isInfinite(prediction)) {
                    // Use mean instead
                    prediction = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                }
            } catch (Exception e) {
                logger.warn("Forecast calculation failed, using historical mean", e);
                prediction = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            }

            // Save forecast result
            forecast.add(prediction);

            // Update history and residuals
            history.poll();
            history.offer(prediction);

            residuals.poll();
            residuals.offer(0.0); // Assume future residuals are 0
        }

        return forecast;
    }

    /**
     * Use exponential smoothing to adjust outliers
     * @param forecast original forecast
     * @param alpha smoothing parameter(0-1)
     * @return smoothed forecast
     */
    public List<Double> smoothForecast(List<Double> forecast, double alpha) {
        if (forecast == null || forecast.isEmpty()) {
            return Collections.emptyList();
        }

        List<Double> smoothed = new ArrayList<>();
        smoothed.add(forecast.get(0)); // First value remains unchanged

        for (int i = 1; i < forecast.size(); i++) {
            double smoothValue = alpha * forecast.get(i) + (1 - alpha) * smoothed.get(i - 1);
            // Ensure value is reasonable
            if (Double.isNaN(smoothValue) || Double.isInfinite(smoothValue)) {
                smoothValue = smoothed.get(i - 1); // Use previous value
            }
            smoothed.add(smoothValue);
        }

        return smoothed;
    }
}
