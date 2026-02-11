import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

public class Lab5Graphics extends JFrame {

    public Lab5Graphics() {
        setTitle("Лабораторна робота №5: Ерміт та Фрактали");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Завдання 1.1 (Ерміт)", new HermitePanel());
        tabbedPane.addTab("Завдання 2.4 (Фрактал Коха)", new KochSquarePanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Lab5Graphics().setVisible(true);
        });
    }
}

// --- ЗАВДАННЯ 1.1: Крива Ерміта ---
class HermitePanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Координати точок P1 та P2 (початкова та кінцева) [cite: 136]
        double x1 = 100, y1 = 400; // P1
        double x2 = 600, y2 = 200; // P2

        // Вектори V1 та V2 (дотичні) [cite: 136]
        // Змінюючи ці значення, можна змінювати форму кривої
        double vx1 = 150, vy1 = -300; // V1 (напрямок вгору-вправо)
        double vx2 = 150, vy2 = 200;  // V2 (напрямок вниз-вправо)

        // Малювання опорних елементів
        g2d.setColor(Color.GRAY);
        drawArrow(g2d, x1, y1, x1 + vx1, y1 + vy1); // Вектор V1
        drawArrow(g2d, x2, y2, x2 + vx2, y2 + vy2); // Вектор V2

        g2d.setColor(Color.RED);
        g2d.fillOval((int)x1-4, (int)y1-4, 8, 8); // Точка P1
        g2d.drawString("P1", (int)x1-10, (int)y1+20);

        g2d.fillOval((int)x2-4, (int)y2-4, 8, 8); // Точка P2
        g2d.drawString("P2", (int)x2+10, (int)y2);

        // Побудова кривої Ерміта
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x1, y1);

        // Формула інтерполяції Ерміта 
        // p(t) = h00(t)*p0 + h10(t)*m0 + h01(t)*p1 + h11(t)*m1
        // Де p0, p1 - точки, m0, m1 - вектори
        for (double t = 0; t <= 1.0; t += 0.01) {
            // Базисні функції Ерміта [cite: 102, 103, 104, 106]
            double h00 = 2 * Math.pow(t, 3) - 3 * Math.pow(t, 2) + 1;
            double h10 = Math.pow(t, 3) - 2 * Math.pow(t, 2) + t;
            double h01 = -2 * Math.pow(t, 3) + 3 * Math.pow(t, 2);
            double h11 = Math.pow(t, 3) - Math.pow(t, 2);

            double x = h00 * x1 + h10 * vx1 + h01 * x2 + h11 * vx2;
            double y = h00 * y1 + h10 * vy1 + h01 * y2 + h11 * vy2;

            path.lineTo(x, y);
        }
        g2d.draw(path);
    }

    // Допоміжний метод для малювання вектора
    private void drawArrow(Graphics2D g2, double x1, double y1, double x2, double y2) {
        g2.draw(new Line2D.Double(x1, y1, x2, y2));
        // Тут можна додати малювання стрілочки на кінці, якщо потрібно
    }
}

// --- ЗАВДАННЯ 2.4: Фрактал Коха на квадраті ---
class KochSquarePanel extends JPanel {
    // Глибина рекурсії (порядок фракталу K) 
    private final int RECURSION_DEPTH = 4;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 100, 0)); // Темно-зелений

        int size = 300; // Сторона квадрата D
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Координати вершин квадрата (центруємо на екрані)
        // Порядок точок важливий для "зовнішньої" побудови
        Point2D.Double p1 = new Point2D.Double(centerX - size/2, centerY - size/2);
        Point2D.Double p2 = new Point2D.Double(centerX + size/2, centerY - size/2);
        Point2D.Double p3 = new Point2D.Double(centerX + size/2, centerY + size/2);
        Point2D.Double p4 = new Point2D.Double(centerX - size/2, centerY + size/2);

        // Малюємо фрактал для кожної сторони квадрата [cite: 143, 144]
        drawKochSegment(g2d, RECURSION_DEPTH, p1, p2); // Верх
        drawKochSegment(g2d, RECURSION_DEPTH, p2, p3); // Право
        drawKochSegment(g2d, RECURSION_DEPTH, p3, p4); // Низ
        drawKochSegment(g2d, RECURSION_DEPTH, p4, p1); // Ліво
    }

    // Рекурсивна функція малювання лінії Коха
    private void drawKochSegment(Graphics2D g, int depth, Point2D.Double p1, Point2D.Double p2) {
        if (depth == 0) {
            g.draw(new Line2D.Double(p1, p2));
        } else {
            // Ділимо відрізок на 3 частини
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;

            // Точка на 1/3 шляху
            Point2D.Double pA = new Point2D.Double(p1.x + dx / 3, p1.y + dy / 3);
            // Точка на 2/3 шляху
            Point2D.Double pB = new Point2D.Double(p1.x + 2 * dx / 3, p1.y + 2 * dy / 3);

            // Знаходимо вершину "трикутника" (поворот на -60 градусів для зовнішнього виступу)
            // Використовуємо матрицю повороту:
            // x' = x*cos(a) - y*sin(a)
            // y' = x*sin(a) + y*cos(a)
            double angle = -Math.PI / 3; // -60 градусів

            double vx = pB.x - pA.x;
            double vy = pB.y - pA.y;

            double tipX = pA.x + vx * Math.cos(angle) - vy * Math.sin(angle);
            double tipY = pA.y + vx * Math.sin(angle) + vy * Math.cos(angle);

            Point2D.Double pTip = new Point2D.Double(tipX, tipY);

            // Рекурсивний виклик для 4-х нових сегментів
            drawKochSegment(g, depth - 1, p1, pA);
            drawKochSegment(g, depth - 1, pA, pTip);
            drawKochSegment(g, depth - 1, pTip, pB);
            drawKochSegment(g, depth - 1, pB, p2);
        }
    }
}
// Необхідний імпорт для Line2D, який міг бути пропущений вище
