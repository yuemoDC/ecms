package com.ecms.service;

import org.springframework.stereotype.Service;
import java.util.*;
import org.apache.commons.math3.linear.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ARIMAPredictionService {

    private static final Logger logger = LoggerFactory.getLogger(ARIMAPredictionService.class);

    public List<Double> forecastARIMA(List<Double> historicalData, int p, int d, int q, int forecastPeriods, int merchantId) {
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

            double[] arParams = estimateARParams(diffData, p);
            double[] maParams = estimateMAParams(diffData, q);
            List<Double> forecastedDiff = forecast(diffData, arParams, maParams, forecastPeriods);
            List<Double> forecastedValues = inverseDifference(forecastedDiff, historicalData, d);

            double mean = historicalData.stream().mapToDouble(Double::doubleValue).average().orElse(100.0);
            double amplitude = 0.1 * mean;
            double damping = 0.03 + (merchantId % 5) * 0.01;

            forecastedValues = addDampedSineFluctuation(forecastedValues, amplitude, damping, merchantId);

            for (int i = 0; i < forecastedValues.size(); i++) {
                if (Double.isNaN(forecastedValues.get(i)) || Double.isInfinite(forecastedValues.get(i))) {
                    forecastedValues.set(i, historicalData.get(historicalData.size() - 1));
                }
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

    private List<Double> generateRandomForecast(double min, double max, int periods) {
        List<Double> forecast = new ArrayList<>();
        double value = min + Math.random() * (max - min);

        for (int i = 0; i < periods; i++) {
            double change = (Math.random() - 0.5) * (max - min) * 0.1;
            value = value + change;
            value = Math.max(min, Math.min(max, value));
            forecast.add(value);
        }
        return forecast;
    }

    private List<Double> difference(List<Double> data) {
        if (data.size() < 2) return Collections.emptyList();
        List<Double> differenced = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            differenced.add(data.get(i) - data.get(i - 1));
        }
        return differenced;
    }

    private List<Double> inverseDifference(List<Double> diffData, List<Double> originalData, int d) {
        if (d == 0 || originalData.isEmpty()) return diffData;

        List<Double> result = new ArrayList<>(diffData);
        double lastValue = originalData.get(originalData.size() - 1);

        for (int i = 0; i < result.size(); i++) {
            lastValue = lastValue + result.get(i);
            result.set(i, lastValue);
        }

        return d > 1 ? inverseDifference(result, originalData, d - 1) : result;
    }

    private double[] estimateARParams(List<Double> data, int p) {
        if (p == 0 || data.size() <= p) return new double[0];
        try {
            double[] acf = computeAutocorrelation(data, p);
            RealMatrix r = new Array2DRowRealMatrix(p, p);
            RealVector b = new ArrayRealVector(p);

            for (int i = 0; i < p; i++) {
                b.setEntry(i, acf[i + 1]);
                for (int j = 0; j < p; j++) {
                    r.setEntry(i, j, acf[Math.abs(i - j)]);
                }
            }

            RealVector solution = new LUDecomposition(r).getSolver().solve(b);
            double[] arParams = new double[p];
            for (int i = 0; i < p; i++) {
                arParams[i] = solution.getEntry(i);
                if (Double.isNaN(arParams[i]) || Double.isInfinite(arParams[i])) return defaultARParams(p);
            }
            return arParams;
        } catch (Exception e) {
            logger.warn("AR parameter estimation failed", e);
            return defaultARParams(p);
        }
    }

    private double[] estimateMAParams(List<Double> data, int q) {
        if (q == 0 || data.size() <= q) return new double[0];
        try {
            double[] residuals = computeResiduals(data);
            double[] acf = computeAutocorrelation(Arrays.stream(residuals).boxed().toList(), q);

            double[] maParams = new double[q];
            for (int i = 0; i < q; i++) {
                maParams[i] = acf[i + 1];
                if (Double.isNaN(maParams[i]) || Double.isInfinite(maParams[i])) {
                    maParams[i] = 0.1 / (i + 1);
                }
            }
            return maParams;
        } catch (Exception e) {
            logger.warn("MA parameter estimation failed", e);
            double[] defaults = new double[q];
            for (int i = 0; i < q; i++) defaults[i] = 0.1 / (i + 1);
            return defaults;
        }
    }

    private double[] computeResiduals(List<Double> data) {
        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return data.stream().mapToDouble(val -> val - mean).toArray();
    }

    private double[] computeAutocorrelation(List<Double> data, int maxLag) {
        double[] result = new double[maxLag + 1];
        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double[] centered = data.stream().mapToDouble(val -> val - mean).toArray();
        double denominator = Arrays.stream(centered).map(v -> v * v).sum();

        for (int k = 0; k <= maxLag; k++) {
            double numerator = 0;
            for (int t = 0; t < data.size() - k; t++) {
                numerator += centered[t] * centered[t + k];
            }
            result[k] = denominator == 0 ? 0 : numerator / denominator;
        }
        result[0] = 1.0;
        return result;
    }

    private double[] defaultARParams(int p) {
        double[] params = new double[p];
        for (int i = 0; i < p; i++) {
            params[i] = 0.5 / (i + 1);
        }
        return params;
    }

    private List<Double> forecast(List<Double> data, double[] arParams, double[] maParams, int periods) {
        List<Double> forecast = new ArrayList<>();
        Queue<Double> residuals = new LinkedList<>(Collections.nCopies(maParams.length, 0.0));
        Queue<Double> history = new LinkedList<>(data.subList(Math.max(0, data.size() - arParams.length), data.size()));

        while (history.size() < arParams.length) history.offer(0.0);

        for (int i = 0; i < periods; i++) {
            double prediction = 0.0;
            List<Double> historyList = new ArrayList<>(history);
            for (int j = 0; j < arParams.length; j++) {
                prediction += arParams[j] * historyList.get(historyList.size() - 1 - j);
            }
            List<Double> residualsList = new ArrayList<>(residuals);
            for (int j = 0; j < maParams.length; j++) {
                prediction += maParams[j] * residualsList.get(residualsList.size() - 1 - j);
            }
            forecast.add(prediction);
            history.poll(); history.offer(prediction);
            residuals.poll(); residuals.offer(0.0);
        }
        return forecast;
    }

    public List<Double> smoothForecast(List<Double> forecast, double alpha) {
        if (forecast == null || forecast.isEmpty()) return Collections.emptyList();

        List<Double> smoothed = new ArrayList<>();
        smoothed.add(forecast.get(0));
        for (int i = 1; i < forecast.size(); i++) {
            double smoothValue = alpha * forecast.get(i) + (1 - alpha) * smoothed.get(i - 1);
            smoothed.add(Double.isFinite(smoothValue) ? smoothValue : smoothed.get(i - 1));
        }
        return smoothed;
    }

    private List<Double> addDampedSineFluctuation(List<Double> forecast, double amplitude, double dampingFactor, int seed) {
        Random rand = new Random(seed);
        List<Double> adjusted = new ArrayList<>();
        for (int i = 0; i < forecast.size(); i++) {
            double cycle = 5 + (seed % 5);
            double sineComponent = amplitude * Math.sin(i * 2 * Math.PI / cycle) * Math.exp(-dampingFactor * i);
            double noise = (rand.nextDouble() - 0.5) * amplitude * (0.2 + (seed % 5) * 0.05);
            adjusted.add(forecast.get(i) + sineComponent + noise);
        }
        return adjusted;
    }
}
