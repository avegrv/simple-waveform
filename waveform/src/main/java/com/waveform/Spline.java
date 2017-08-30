package com.waveform;

class Spline {

    private SplinePart[] parts;
    private double[] alpha;
    private double[] beta;

    Spline(int n) {
        parts = new SplinePart[n];
        for (int i = 0; i < n; ++i) {
            parts[i] = new SplinePart();
        }
        alpha = new double[n - 1];
        beta = new double[n - 1];
    }

    double interpolate(double x) {
        int n = parts.length;
        SplinePart s;

        if (x <= parts[0].x) {
            s = parts[0];
        } else if (x >= parts[n - 1].x) {
            s = parts[n - 1];
        } else {
            int i = 0;
            int j = n - 1;
            while (i + 1 < j) {
                int k = i + (j - i) / 2;
                if (x <= parts[k].x) {
                    j = k;
                } else {
                    i = k;
                }
            }
            s = parts[j];
        }
        double dx = x - s.x;
        return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
    }

    void build(double[] x, double[] y) {
        int n = parts.length;
        for (int i = 0; i < n; ++i) {
            parts[i].x = x[i];
            parts[i].a = y[i];
            parts[i].b = 0;
            parts[i].c = 0;
            parts[i].d = 0;
        }
        parts[0].c = parts[n - 1].c = 0.0;

        alpha[0] = beta[0] = 0.0;
        for (int i = 1; i < n - 1; ++i) {
            double hi = x[i] - x[i - 1];
            double hi1 = x[i + 1] - x[i];
            double A = hi;
            double C = 2.0 * (hi + hi1);
            double B = hi1;
            double F = 6.0 * ((y[i + 1] - y[i]) / hi1 - (y[i] - y[i - 1]) / hi);
            double z = (A * alpha[i - 1] + C);
            alpha[i] = -B / z;
            beta[i] = (F - A * beta[i - 1]) / z;
        }

        for (int i = n - 2; i > 0; --i) {
            parts[i].c = alpha[i] * parts[i + 1].c + beta[i];
        }

        for (int i = n - 1; i > 0; --i) {
            double hi = x[i] - x[i - 1];
            parts[i].d = (parts[i].c - parts[i - 1].c) / hi;
            parts[i].b = hi * (2.0 * parts[i].c + parts[i - 1].c) / 6.0 + (y[i] - y[i - 1]) / hi;
        }
    }

    private class SplinePart {
        double a, b, c, d, x;
    }
}

