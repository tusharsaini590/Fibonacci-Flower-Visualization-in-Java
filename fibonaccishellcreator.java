package COA_Petprj;

// import necessary javafx, swing, and awt libraries
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

// this class creates a fibonacci shell pattern using a user-selected petal image
public class fibonaccishellcreator extends JPanel {
    // images for the petal and the final shell
    private BufferedImage petal, shell;
    // dimensions of the panel
    private static final int width = 800, height = 600;
    // number of petals in the shell
    private int numpetals = 10;
    // factor by which each petal is scaled compared to the previous one
    private double scalefactor = 1.2;
    // color adjustment parameters
    private float hue = 0.0f, saturation = 1.0f, brightness = 1.0f;
    // zoom factor
    private double zoomfactor = 1.0;

    // constructor: sets up the panel and prompts user to select a petal image
    public fibonaccishellcreator() {
        setPreferredSize(new Dimension(width, height));
        // use javafx's filechooser for better file selection ui
        Platform.runLater(() -> {
            FileChooser filechooser = new FileChooser();
            filechooser.setTitle("SELECT PETAL IMAGE");
            // allow only image files to be selected
            filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image files", "*.jpg", "*.png", "*.gif"));
            File selectedfile = filechooser.showOpenDialog(null);
            if (selectedfile != null && selectedfile.exists()) {
                try {
                    // load the selected image as the petal
                    petal = ImageIO.read(selectedfile);
                    // create the shell pattern
                    createshell();
                    // refresh the display
                    repaint();
                } catch (IOException e) {
                    // show an error message if the image couldn't be read
                    JOptionPane.showMessageDialog(this, "failed to read the selected image.", "error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // warn the user if no file was selected
                JOptionPane.showMessageDialog(this, "no file was selected or the file does not exist or you are wasting your time fool.", "warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // creates the fibonacci flower pattern
    private void createshell() {
        // create a new blank image for the shell
        shell = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = shell.createGraphics();
        // enable anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // use bilinear interpolation for better image scaling
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // calculate the center of the image
        int centerx = width / 2, centery = height / 2;
        // golden ratio for fibonacci spiral

        double goldenratio = (1 + Math.sqrt(5)) / 2, angle = 0, scale = 0.125 * zoomfactor;
        // draw each petal
        double dynamicangle = 2 * Math.PI / 9;  // distribute petals evenly
        for (int i = 0; i < numpetals; i++) {
            drawpetal(g2d, centerx, centery, goldenratio, angle, scale, i);
            // rotate by certain degrees for next petal
            angle += dynamicangle;
            // increase the scale for next petal
            scale *= scalefactor;
        }

        g2d.dispose();
    }

    private void drawpetal(Graphics2D g2d, int centerx, int centery, double goldenratio, double angle, double scale, int iteration) {
        // calculate the radius based on the golden ratio
        double radiusadjustment = Math.max(1, Math.log(numpetals) / Math.log(2));
        double radius = Math.pow(goldenratio, iteration / radiusadjustment) * 20  ;

        // calculate the position of the petal
        int x = (int) (centerx + radius * Math.cos(angle));
        int y = (int) (centery + radius * Math.sin(angle));

        // create a transformation for positioning and scaling the petal
        AffineTransform at = new AffineTransform();
        at.translate(x, y);

        // rotate by angle + 20 degrees to point the root towards the center
        at.rotate(angle + Math.PI / 2.9);

        // adjust the overlap factor to control petal overlap
        double overlapfactor = 1.1;
        at.scale(scale * 0.98 * overlapfactor, scale * 0.98 * overlapfactor);

        // center the petal on its position
        at.translate(-petal.getWidth() / 2.0, -petal.getHeight() / 2.0);

        // draw the petal with color adjustments
        g2d.drawImage(adjustcolor(petal, hue, saturation, brightness), at, null);

        // increment the angle for the next petal
        double dynamicangle = 2 * Math.PI / numpetals;
        angle += dynamicangle;
    }


    // adjusts the color of the petal image
    private BufferedImage adjustcolor(BufferedImage original, float hue, float saturation, float brightness) {
        BufferedImage adjusted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        // adjust each pixel's color
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Color color = new Color(original.getRGB(x, y), true);
                // convert rgb to hsb
                float[] hsbvalues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                // apply color adjustments
                hsbvalues[0] += hue; hsbvalues[1] *= saturation; hsbvalues[2] *= brightness;
                // convert back to rgb and set the pixel color
                adjusted.setRGB(x, y, (Color.HSBtoRGB(hsbvalues[0], hsbvalues[1], hsbvalues[2]) & 0x00ffffff) | (color.getAlpha() << 24));
            }
        }
        return adjusted;
    }

    // overridden method to paint the component
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the shell if it has been created
        if (shell != null) g.drawImage(shell, 0, 0, this);
    }

    // main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // set the look and feel to match the system
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // initialize javafx
                new JFXPanel();
            } catch (Exception e) { e.printStackTrace(); }
            // create the main frame
            JFrame frame = new JFrame("fibonacci shell");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fibonaccishellcreator creator = new fibonaccishellcreator();
            // create a panel for settings
            JPanel settingspanel = new JPanel(new GridLayout(6, 2));
            // spinner for number of petals
            JSpinner numpetalsspinner = new JSpinner(new SpinnerNumberModel(13, 1, 200, 1));
            numpetalsspinner.addChangeListener(e -> { creator.numpetals = (int) numpetalsspinner.getValue(); creator.createshell(); creator.repaint(); });
            // spinner for scaling factor
            JSpinner scalefactorspinner = new JSpinner(new SpinnerNumberModel(1.3, 0.1, 5.0, 0.1));
            scalefactorspinner.addChangeListener(e -> { creator.scalefactor = (double) scalefactorspinner.getValue(); creator.createshell(); creator.repaint(); });
            // slider for hue adjustment
            JSlider hueslider = new JSlider(-180, 180, 0);
            hueslider.addChangeListener(e -> { creator.hue = hueslider.getValue() / 360.0f; creator.createshell(); creator.repaint(); });
            // slider for saturation adjustment
            JSlider saturationslider = new JSlider(0, 200, 100);
            saturationslider.addChangeListener(e -> { creator.saturation = saturationslider.getValue() / 100.0f; creator.createshell(); creator.repaint(); });
            // slider for brightness adjustment
            JSlider brightnessslider = new JSlider(0, 200, 100);
            brightnessslider.addChangeListener(e -> { creator.brightness = brightnessslider.getValue() / 100.0f; creator.createshell(); creator.repaint(); });
            // slider for zoom adjustment
            JSlider zoomslider = new JSlider(10, 200, 100);
            zoomslider.addChangeListener(e -> {
                creator.zoomfactor = Math.pow(zoomslider.getValue() / 100.0, 1.5);  // non-linear zoom
                creator.createshell();
                creator.repaint();
            });
            // add all components to the settings panel
            settingspanel.add(new JLabel("NUMBER OF PETALS:")); settingspanel.add(numpetalsspinner);
            settingspanel.add(new JLabel("SCALING FACTOR:")); settingspanel.add(scalefactorspinner);
            settingspanel.add(new JLabel("HUE:")); settingspanel.add(hueslider);
            settingspanel.add(new JLabel("SATURATION:")); settingspanel.add(saturationslider);
            settingspanel.add(new JLabel("BRIGHTNESS:")); settingspanel.add(brightnessslider);
            settingspanel.add(new JLabel("ZOOM:")); settingspanel.add(zoomslider);
            // added components to the frame
            frame.add(settingspanel, BorderLayout.NORTH);
            frame.add(creator, BorderLayout.CENTER);
            // finalize and display the frame
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
