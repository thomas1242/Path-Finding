import java.awt.*;

public class Interpolation {
    
    public static Color[] interpolateColors(Color startColor, Color endColor, int length) {
        return interpolateColors(startColor.getRGB(), endColor.getRGB(), length);
    }

    public static Color[] interpolateColors(int startRGB, int endRGB, int length) {
        Color[] colors = new Color[length];

        colors[0] 		   = new Color(startRGB);
        colors[length - 1] = new Color(endRGB);

        double R = startRGB >> 16 & 0xFF;
        double G = startRGB >> 8  & 0xFF;
        double B = startRGB       & 0xFF;

        double delta_R = ((endRGB >> 16 & 0xFF) - (startRGB >> 16 & 0xFF)) / 1.0 / length;      
        double delta_G = ((endRGB >> 8  & 0xFF) - (startRGB >> 8  & 0xFF)) / 1.0 / length;
        double delta_B = ((endRGB       & 0xFF) - (startRGB       & 0xFF)) / 1.0 / length;

        for (int i = 1; i < length - 1; i++) {   // fill 1D array with interpolated colors
            R += delta_R;
            G += delta_G;
            B += delta_B;

            int intARGB = 0xFF << 24 | (int)R << 16 | (int)G << 8 | (int)B;
            colors[i] = new Color( intARGB );
        }

        return colors;
    }

}