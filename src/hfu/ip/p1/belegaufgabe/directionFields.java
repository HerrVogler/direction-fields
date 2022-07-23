package hfu.ip.p1.belegaufgabe;

import edu.princeton.cs.algs4.StdDraw;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Scanner;

/**
 * Die Klasse directionFields nimmt verschiedene Differentialgleichungen erster Ordnung
 * als Eingabe über die Konsole und stellt das jeweilige Richtungsfeld und
 * einige zugehörige Isoklinen grafisch dar.
 *
 * @author Jakob Vogler
 */
public class directionFields {
    /**
     * Genauigkeit/Skalierung der xy-Achsen
     */
    private static int scale = 1000;

    /**
     * Schrittweite für die Steigungstangenten im Richtungsfeld
     */
    private static double increment = 0.1;

    /**
     * Skalierung bis zum Rahmen des Koordinatensystems
     */
    private static double coordinateScale;

    /**
     * Die Differentialgleichung erster Ordnung
     */
    private static String expression;

    /**
     * Status zur Animation des Erscheinens der grafischen Oberfläche
     */
    private static boolean animated = false;

    /**
     * Implementiert das Eingabemenü und ruft die draw Methode auf.
     */
    public static void main(String[] args) {
        String suggestion = "";
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        PropertyResourceBundle res = (PropertyResourceBundle) PropertyResourceBundle.getBundle("hfu.ip.p1.belegaufgabe.menuItems", Locale.GERMAN);

        boolean run = true;

        while (run) {
            boolean suggestionAvailable = !suggestion.equals("");

            System.out.println( " --- " + res.getString("menu") + " ---\n" +
                    " - " + res.getString("diffEquations") + " -\n" +
                    (suggestionAvailable ? "     - 0: y' = " + suggestion + "\n" : "") +
                    "     - 1: y' = 2xy\n" +
                    "     - 2: y' = - x / y\n" +
                    "     - 3: y' = x * x + y - 0.1\n" +
                    "     - 4: " + res.getString("custom") + "\n" +
                    " - " + res.getString("options") + " -\n" +
                    "     " + (increment != 0.2 ? "-" : " ") + " 5: " + res.getString("lessElements") + "\n" +
                    "     " + (increment != 0.05 ? "-" : " ") + " 6: " + res.getString("moreElements") + "\n" +
                    "     - 7: " + (animated ? res.getString("turnOff") : res.getString("turnOn")) + "\n" +
                    "     - 8: " + res.getString("quit") + "\n");

            System.out.print(" -  ");

            switch (sc.nextLine().replaceAll(" ", "")) {
                case "0":
                    if (suggestionAvailable && !expression.equals("2xy") && !expression.equals("- x / y") && !expression.equals("x * x + y - 0.1")) {
                        String helper = expression;
                        expression = suggestion;
                        suggestion = helper;
                        draw();
                    } else if (suggestionAvailable) {
                        expression = suggestion;
                        draw();
                    }

                    break;
                case "1":
                    expression = "2xy";
                    draw();
                    break;
                case "2":
                    expression = "- x / y";
                    draw();
                    break;
                case "3":
                    expression = "x * x + y - 0.1";
                    draw();
                    break;
                case "4":
                    System.out.print("\ny' = ");
                    expression = sc.nextLine().toLowerCase();
                    suggestion = expression;
                    if (!expression.replaceAll(" ", "").equals(""))
                        draw();
                    break;
                case "5":
                    if (increment != 0.2)
                        increment *= 2;

                    break;
                case "6":
                    if (increment != 0.05)
                        increment /= 2;

                    break;
                case "7":
                    if (animated)
                        scale = 1000;
                    else
                        scale = 300;

                    animated = !animated;
                    break;
                case "8":
                    run = false;
                    break;
            }

            System.out.println();
        }

        System.exit(0);
    }

    /**
     * Ruft Befehle und Methoden zur Darstellung der Differentialgleichung auf.
     */
    static void draw() {
        StdDraw.setCanvasSize(700, 700);

        // Pausieren der Ausgabe bis zum show() Aufruf -> kein Buffering/"Ladezeit"/Animation
        if (!animated)
            StdDraw.enableDoubleBuffering();
        else
            StdDraw.disableDoubleBuffering();

        // Definieren der Bildskalierung mit Berücksichtigen des Steigungsbereichs,
        // des Koordinatensystems, und der Beschriftungen
        coordinateScale = scale + 0.65 * increment * scale;
        double windowScale = coordinateScale + scale * 0.25;
        StdDraw.setXscale(-windowScale, coordinateScale + scale * 0.15);
        StdDraw.setYscale(-windowScale, windowScale);

        drawCoordinateSystem();

        // Berechnen und Darstellen der Steigungen bei x und y
        StdDraw.setPenColor(Color.BLUE);

        BigDecimal increment = new BigDecimal(String.format("%.2f", directionFields.increment));

        for (BigDecimal y = new BigDecimal("-1"); y.doubleValue() <= 1; y = y.add(increment)) {
            for (BigDecimal x = new BigDecimal("-1"); x.doubleValue() <= 1; x = x.add(increment)) {
                drawSlope(x.doubleValue(), y.doubleValue());
            }
        }

        // Aufrufen der Methoden zur Darstellung der Isoklinen
        StdDraw.setPenColor(Color.GREEN);
        drawIsoline(-1);

        StdDraw.setPenColor(Color.RED);
        drawIsoline(0);

        StdDraw.setPenColor(Color.MAGENTA);
        drawIsoline(1);

        StdDraw.setPenColor(Color.PINK);
        drawIsoline(Double.MAX_VALUE);

        StdDraw.show();
    }

    /**
     * Stellt ein Koordinatensystem mit Beschriftung grafisch dar.
     */
    static void drawCoordinateSystem() {
        StdDraw.setPenColor(Color.BLACK);

        // x-Richtung
        StdDraw.line(-coordinateScale, -coordinateScale, coordinateScale, -coordinateScale); // bottom
        StdDraw.line(-coordinateScale, coordinateScale, coordinateScale, coordinateScale); // top
        StdDraw.line(-coordinateScale, 0, coordinateScale, 0); // middle

        // y-Richtung
        StdDraw.line(-coordinateScale, -coordinateScale, -coordinateScale, coordinateScale); // left
        StdDraw.line(coordinateScale, -coordinateScale, coordinateScale, coordinateScale); // right
        StdDraw.line(0, -coordinateScale, 0, coordinateScale); // middle

        String format = "%."+ (increment < 0.1 ? "2" : "1") + "f";
        // x-Bezeichnungen
        for (double i = -1; i <= 1; i += 5 * increment) {
            StdDraw.line(scale * i, -coordinateScale - coordinateScale * 0.02, scale * i, -coordinateScale + coordinateScale * 0.02);
            StdDraw.text(scale * i, -coordinateScale - scale * 0.08, String.format(format, i));
        }

        StdDraw.text(0, -coordinateScale - scale * 0.18, "x");

        // y-Bezeichnungen
        for (double i = -1; i <= 1; i += 5 * increment) {
            StdDraw.line(-coordinateScale - coordinateScale * 0.02, scale * i, -coordinateScale + coordinateScale * 0.02, scale * i);
            StdDraw.text(-coordinateScale - scale * 0.1, scale * i, String.format(format, i));
        }

        StdDraw.text(-coordinateScale - scale * 0.20, 0, "y");

        // Legende für die Isoklinen
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.text(-scale * 0.75, coordinateScale + scale * 0.08, "c = -1");

        StdDraw.setPenColor(Color.RED);
        StdDraw.text(-scale * 0.25, coordinateScale + scale * 0.08, "c = 0");

        StdDraw.setPenColor(Color.MAGENTA);
        StdDraw.text(scale * 0.25, coordinateScale + scale * 0.08, "c = 1");

        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(scale * 0.75, coordinateScale + scale * 0.08, "c = ±∞");
    }

    /**
     * Stellt eine Tangente zur Steigung am Punkt (x | y) im Bereich<br><br>
     *
     * x - 0.35 * {@link #increment} ≤ x ≤ x + 0.35 * {@link #increment},<br>
     * y - 0.35 * {@link #increment} ≤ y ≤ y + 0.35 * {@link #increment}<br><br>
     *
     *  grafisch dar.
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    static void drawSlope(double x, double y) {
        if (x == 0)
            x += Double.MIN_VALUE;
        if (y == 0)
            y += Double.MIN_VALUE;

        // Berechnet die Steigung bei (x | y)
        double m;
        try {
            m = new ExpressionBuilder(expression).variables("x", "y").build().setVariable("x", x).setVariable("y", y).evaluate();
        } catch (ArithmeticException e) {
            m = Double.MAX_VALUE;
        }

        // Berechnet den y-Achsenabschnitt
        double b = y - (m * x);

        // Anpassen an die Skalierung
        x *= scale;
        y *= scale;
        b *= scale;

        double range = 0.35 * increment * scale;
        double minX, minY, maxX, maxY;

        // Berechnen der zwei Punkte für die Steigungsgerade
        if (Math.abs(m) > 1) {
            minY = y - range;
            maxY = y + range;

            minX = (minY - b) / m;
            maxX = (maxY - b) / m;
        } else {
            minX = x - range;
            maxX = x + range;

            minY = (m * minX) + b;
            maxY = (m * maxX) + b;
        }

        if (!Double.isFinite(minX))
            minX = x;
        if (!Double.isFinite(maxX))
            maxX = x;

        StdDraw.line(minX, minY, maxX, maxY);
    }

    /**
     * Stellt die Isokline für die jeweilige Steigung grafisch dar.
     *
     * @param c y'(x), der von der Isokline abgebildete Wert
     */
    static void drawIsoline(double c) {
        // Ausnahmen für nicht-Funktionsgleichungen nach Umstellung nach y
        if ((expression.equals("- x / y") && c == 0) || (expression.equals("(y-x)/(y+x)") && c == 1)) {
            StdDraw.line(0, -coordinateScale + 1, 0,  coordinateScale - 1);
            return;
        }
        if (expression.equals("2xy") && c == 0)
            StdDraw.line(0, -coordinateScale + 1, 0,  coordinateScale - 1);

        // Berechnen und Darstellen der Isokline durch Annäherung
        for (double x = -coordinateScale + 1; x < coordinateScale; x++) {
            double y0 = calculateIsoline(x - 1, c) * scale;
            double y1 = calculateIsoline(x + 1, c) * scale;

            if (Double.isNaN(y0) || Double.isNaN(y1) || Math.abs(y0) > coordinateScale || Math.abs(y1) > coordinateScale)
                continue;

            StdDraw.line(x - 1, y0, x + 1, y1);
        }
    }

    /**
     * Berechnet mit der Steigung und der x-Koordinate für ausgewählte
     * Differentialgleichungen die y-Koordinate der jeweiligen Isokline.
     *
     * @param x x-Koordinate
     * @param c y'(x), der von der Isokline abgebildete Wert
     * @return y-Koordinate
     */
    static double calculateIsoline(double x, double c) {
        x /= scale;

        return switch (expression) {
            case "x + y" -> c - x;
            case "- x / y" -> -x / c;
            case "x * x + y - 0.1" -> c - x * x + 0.1;
            case "2xy" -> c / (2 * x);
            case "x/(3sqrt(1+xx)yy)" -> Math.sqrt(x/(3 * Math.sqrt(1 + x * x) * c));
            case "-0.5exp(2x)sin(x)+y" -> c + 0.5 * Math.exp(2 * x) * Math.sin(x);
            case "(y-x)/(y+x)" -> x * ((1 + c)/(1 - c));
            default -> 2;
        };
    }
}
