package cbir;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageDescriptors {
	final static boolean[] isUniform = new boolean[257];
	final static int[] uniformPatterns = { 0, 1, 2, 3, 4, 6, 7, 8, 12, 14, 15, 16, 24, 28, 30, 31, 32, 48, 56,
			60, 62, 63, 64, 96, 112, 120, 124, 126, 127, 128, 129, 131, 135, 143, 159, 191, 192, 193, 195,
			199, 207, 223, 224, 225, 227, 231, 239, 240, 241, 243, 247, 248, 249, 251, 252, 253, 254, 255 };
	static {
		for (int p : uniformPatterns) {
			isUniform[p] = true;
		}
	}

	static int[] colorHistogram(BufferedImage img, int binsPerColor) {
		final int[][][] histogram = new int[binsPerColor][binsPerColor][binsPerColor];
		final int div = (int) Math.ceil(256d / binsPerColor);
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(img.getRGB(j, i));
				histogram[c.getRed() / div][c.getGreen() / div][c.getBlue() / div]++;
			}
		}
		return MathUtils.flatten(histogram);
	}

	static int[] computeImageDescriptor(BufferedImage img, int binsPerColor, boolean useLbp,
			boolean useColor) {
		if (useLbp && useColor) {
			return MathUtils.concat(lbp(img), colorHistogram(img, binsPerColor));
		}
		if (useLbp) {
			return lbp(img);
		}
		return colorHistogram(img, binsPerColor);
	}

	static int[] lbp(BufferedImage image) {
		int[][] img = ImageDescriptors.imagetoGreycale2dArray(image);
		int sz = 1;
		int width = img.length;
		int height = img[0].length;
		int[] histogram = new int[257];
		for (int i = sz; i < width - sz; i++) {
			for (int j = sz; j < height - sz; j++) {
				int pow = 1;
				int sum = 0;
				int center = img[i][j];
				for (int ii = -sz; ii <= sz; ii++) {
					for (int jj = -sz; jj <= sz; jj++) {
						if (ii != 0 || jj != 0) {
							if (center >= img[i + ii][j + jj]) {
								sum += pow;
							}
							pow *= 2;
						}
					}
				}
				if (isUniform[sum]) {
					histogram[sum]++;
				} else {
					histogram[256]++;
				}
			}
		}
		return histogram;
	}

	static int[][] imagetoGreycale2dArray(BufferedImage img) {
		int[][] arr = new int[img.getHeight()][img.getWidth()];

		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[0].length; j++) {
				Color c = new Color(img.getRGB(j, i));
				// Y = 0.2989 R + 0.5870 G + 0.1140 B
				int grey = (int) (0.2989 * c.getRed() + 0.5870 * c.getGreen() + 0.1140 * c.getBlue());
				arr[i][j] = grey;
			}
		return arr;
	}

}
