package com.application.tippzi.swipeview;

/**
 * Created by mukeshsolanki on 01/11/17.
 */

class LinearRegression {
    private final double alpha, beta;

    LinearRegression(float[] x, float[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }
        int n = x.length;
        double sumx = 0.0, sumy = 0.0;
        for (float aX : x) sumx += aX;
        for (int i = 0; i < n; i++) sumy += y[i];
        double xbar = sumx / n;
        double ybar = sumy / n;
        double xxbar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        beta = xybar / xxbar;
        alpha = ybar - beta * xbar;
    }

    double intercept() {
        return alpha;
    }

    double slope() {
        return beta;
    }
}