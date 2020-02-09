package android.support.graphics.drawable;

import android.graphics.Path;
import android.util.Log;
import java.util.ArrayList;

class PathParser {
    private static final String LOGTAG = "PathParser";

    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    public static class PathDataNode {
        float[] params;
        char type;

        PathDataNode(char c, float[] fArr) {
            this.type = c;
            this.params = fArr;
        }

        PathDataNode(PathDataNode pathDataNode) {
            this.type = pathDataNode.type;
            this.params = PathParser.copyOfRange(pathDataNode.params, 0, pathDataNode.params.length);
        }

        public static void nodesToPath(PathDataNode[] pathDataNodeArr, Path path) {
            float[] fArr = new float[6];
            char c = 'm';
            for (int i = 0; i < pathDataNodeArr.length; i++) {
                addCommand(path, fArr, c, pathDataNodeArr[i].type, pathDataNodeArr[i].params);
                c = pathDataNodeArr[i].type;
            }
        }

        public void interpolatePathDataNode(PathDataNode pathDataNode, PathDataNode pathDataNode2, float f) {
            for (int i = 0; i < pathDataNode.params.length; i++) {
                this.params[i] = (pathDataNode.params[i] * (1.0f - f)) + (pathDataNode2.params[i] * f);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0056, code lost:
            r25 = r9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x014f, code lost:
            r2 = r0;
            r3 = r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x0232, code lost:
            r2 = r0;
            r3 = r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x02e6, code lost:
            r3 = r7;
            r2 = r8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x02e8, code lost:
            r9 = r25 + r20;
            r0 = r30;
         */
        private static void addCommand(Path path, float[] fArr, char c, char c2, float[] fArr2) {
            int i;
            int i2;
            int i3;
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11;
            float f12;
            float f13;
            Path path2 = path;
            float[] fArr3 = fArr2;
            float f14 = fArr[0];
            float f15 = fArr[1];
            float f16 = fArr[2];
            float f17 = fArr[3];
            float f18 = fArr[4];
            float f19 = fArr[5];
            switch (c2) {
                case 'A':
                case 'a':
                    i = 7;
                    break;
                case 'C':
                case 'c':
                    i = 6;
                    break;
                case 'H':
                case 'V':
                case 'h':
                case 'v':
                    i = 1;
                    break;
                case 'Q':
                case 'S':
                case 'q':
                case 's':
                    i = 4;
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    path2.moveTo(f18, f19);
                    f14 = f18;
                    f16 = f14;
                    f15 = f19;
                    f17 = f15;
                    break;
            }
            i = 2;
            float f20 = f14;
            float f21 = f15;
            float f22 = f18;
            float f23 = f19;
            int i4 = 0;
            char c3 = c;
            while (i4 < fArr3.length) {
                float f24 = 0.0f;
                switch (c2) {
                    case 'A':
                        i3 = i4;
                        int i5 = i3 + 5;
                        int i6 = i3 + 6;
                        drawArc(path, f20, f21, fArr3[i5], fArr3[i6], fArr3[i3 + 0], fArr3[i3 + 1], fArr3[i3 + 2], fArr3[i3 + 3] != 0.0f, fArr3[i3 + 4] != 0.0f);
                        f = fArr3[i5];
                        f2 = fArr3[i6];
                        break;
                    case 'C':
                        i2 = i4;
                        int i7 = i2 + 2;
                        int i8 = i2 + 3;
                        int i9 = i2 + 4;
                        int i10 = i2 + 5;
                        path.cubicTo(fArr3[i2 + 0], fArr3[i2 + 1], fArr3[i7], fArr3[i8], fArr3[i9], fArr3[i10]);
                        f20 = fArr3[i9];
                        float f25 = fArr3[i10];
                        float f26 = fArr3[i7];
                        float f27 = fArr3[i8];
                        f21 = f25;
                        f17 = f27;
                        f16 = f26;
                        break;
                    case 'H':
                        i2 = i4;
                        int i11 = i2 + 0;
                        path2.lineTo(fArr3[i11], f21);
                        f20 = fArr3[i11];
                        break;
                    case 'L':
                        i2 = i4;
                        int i12 = i2 + 0;
                        int i13 = i2 + 1;
                        path2.lineTo(fArr3[i12], fArr3[i13]);
                        f20 = fArr3[i12];
                        f21 = fArr3[i13];
                        break;
                    case 'M':
                        i2 = i4;
                        int i14 = i2 + 0;
                        f20 = fArr3[i14];
                        int i15 = i2 + 1;
                        f21 = fArr3[i15];
                        if (i2 <= 0) {
                            path2.moveTo(fArr3[i14], fArr3[i15]);
                            f23 = f21;
                            f22 = f20;
                            break;
                        } else {
                            path2.lineTo(fArr3[i14], fArr3[i15]);
                            break;
                        }
                    case 'Q':
                        i2 = i4;
                        int i16 = i2 + 0;
                        int i17 = i2 + 1;
                        int i18 = i2 + 2;
                        int i19 = i2 + 3;
                        path2.quadTo(fArr3[i16], fArr3[i17], fArr3[i18], fArr3[i19]);
                        f4 = fArr3[i16];
                        f3 = fArr3[i17];
                        f5 = fArr3[i18];
                        f6 = fArr3[i19];
                        break;
                    case 'S':
                        float f28 = f21;
                        float f29 = f20;
                        i2 = i4;
                        if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                            float f30 = (f29 * 2.0f) - f16;
                            f7 = (f28 * 2.0f) - f17;
                            f8 = f30;
                        } else {
                            f8 = f29;
                            f7 = f28;
                        }
                        int i20 = i2 + 0;
                        int i21 = i2 + 1;
                        int i22 = i2 + 2;
                        int i23 = i2 + 3;
                        path.cubicTo(f8, f7, fArr3[i20], fArr3[i21], fArr3[i22], fArr3[i23]);
                        f4 = fArr3[i20];
                        f3 = fArr3[i21];
                        f5 = fArr3[i22];
                        f6 = fArr3[i23];
                        break;
                    case 'T':
                        float f31 = f21;
                        float f32 = f20;
                        i2 = i4;
                        if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                            f31 = (f31 * 2.0f) - f17;
                            f32 = (f32 * 2.0f) - f16;
                        }
                        int i24 = i2 + 0;
                        int i25 = i2 + 1;
                        path2.quadTo(f32, f31, fArr3[i24], fArr3[i25]);
                        f20 = fArr3[i24];
                        f21 = fArr3[i25];
                        f16 = f32;
                        f17 = f31;
                        break;
                    case 'V':
                        i2 = i4;
                        int i26 = i2 + 0;
                        path2.lineTo(f20, fArr3[i26]);
                        f21 = fArr3[i26];
                        break;
                    case 'a':
                        int i27 = i4 + 5;
                        float f33 = fArr3[i27] + f20;
                        int i28 = i4 + 6;
                        float f34 = f21;
                        float f35 = f20;
                        boolean z = fArr3[i4 + 3] != 0.0f;
                        i3 = i4;
                        drawArc(path, f20, f21, f33, fArr3[i28] + f21, fArr3[i4 + 0], fArr3[i4 + 1], fArr3[i4 + 2], z, fArr3[i4 + 4] != 0.0f);
                        f = f35 + fArr3[i27];
                        f2 = f34 + fArr3[i28];
                        break;
                    case 'c':
                        int i29 = i4 + 2;
                        int i30 = i4 + 3;
                        int i31 = i4 + 4;
                        int i32 = i4 + 5;
                        path.rCubicTo(fArr3[i4 + 0], fArr3[i4 + 1], fArr3[i29], fArr3[i30], fArr3[i31], fArr3[i32]);
                        f10 = fArr3[i29] + f20;
                        f9 = fArr3[i30] + f21;
                        f20 += fArr3[i31];
                        f21 += fArr3[i32];
                        break;
                    case 'h':
                        int i33 = i4 + 0;
                        path2.rLineTo(fArr3[i33], 0.0f);
                        f20 += fArr3[i33];
                        break;
                    case 'l':
                        int i34 = i4 + 0;
                        int i35 = i4 + 1;
                        path2.rLineTo(fArr3[i34], fArr3[i35]);
                        f20 += fArr3[i34];
                        f21 += fArr3[i35];
                        break;
                    case 'm':
                        int i36 = i4 + 0;
                        f20 += fArr3[i36];
                        int i37 = i4 + 1;
                        f21 += fArr3[i37];
                        if (i4 <= 0) {
                            path2.rMoveTo(fArr3[i36], fArr3[i37]);
                            f23 = f21;
                            f22 = f20;
                            break;
                        } else {
                            path2.rLineTo(fArr3[i36], fArr3[i37]);
                            break;
                        }
                    case 'q':
                        int i38 = i4 + 0;
                        int i39 = i4 + 1;
                        int i40 = i4 + 2;
                        int i41 = i4 + 3;
                        path2.rQuadTo(fArr3[i38], fArr3[i39], fArr3[i40], fArr3[i41]);
                        f10 = fArr3[i38] + f20;
                        f9 = fArr3[i39] + f21;
                        f20 += fArr3[i40];
                        f21 += fArr3[i41];
                        break;
                    case 's':
                        if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                            float f36 = f20 - f16;
                            f11 = f21 - f17;
                            f12 = f36;
                        } else {
                            f12 = 0.0f;
                            f11 = 0.0f;
                        }
                        int i42 = i4 + 0;
                        int i43 = i4 + 1;
                        int i44 = i4 + 2;
                        int i45 = i4 + 3;
                        path.rCubicTo(f12, f11, fArr3[i42], fArr3[i43], fArr3[i44], fArr3[i45]);
                        f10 = fArr3[i42] + f20;
                        f9 = fArr3[i43] + f21;
                        f20 += fArr3[i44];
                        f21 += fArr3[i45];
                        break;
                    case 't':
                        if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                            f24 = f20 - f16;
                            f13 = f21 - f17;
                        } else {
                            f13 = 0.0f;
                        }
                        int i46 = i4 + 0;
                        int i47 = i4 + 1;
                        path2.rQuadTo(f24, f13, fArr3[i46], fArr3[i47]);
                        float f37 = f24 + f20;
                        float f38 = f13 + f21;
                        f20 += fArr3[i46];
                        f21 += fArr3[i47];
                        f17 = f38;
                        f16 = f37;
                        break;
                    case 'v':
                        int i48 = i4 + 0;
                        path2.rLineTo(0.0f, fArr3[i48]);
                        f21 += fArr3[i48];
                        break;
                    default:
                        float f39 = f21;
                        float f40 = f20;
                        break;
                }
            }
            float f41 = f21;
            fArr[0] = f20;
            fArr[1] = f41;
            fArr[2] = f16;
            fArr[3] = f17;
            fArr[4] = f22;
            fArr[5] = f23;
        }

        private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z, boolean z2) {
            double d;
            double d2;
            float f8 = f;
            float f9 = f3;
            float f10 = f5;
            float f11 = f6;
            boolean z3 = z2;
            double radians = Math.toRadians((double) f7);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d3 = (double) f8;
            Double.isNaN(d3);
            double d4 = d3 * cos;
            double d5 = d3;
            double d6 = (double) f2;
            Double.isNaN(d6);
            double d7 = d4 + (d6 * sin);
            double d8 = (double) f10;
            Double.isNaN(d8);
            double d9 = d7 / d8;
            double d10 = (double) (-f8);
            Double.isNaN(d10);
            double d11 = d10 * sin;
            Double.isNaN(d6);
            double d12 = d11 + (d6 * cos);
            double d13 = d6;
            double d14 = (double) f11;
            Double.isNaN(d14);
            double d15 = d12 / d14;
            double d16 = (double) f9;
            Double.isNaN(d16);
            double d17 = d16 * cos;
            double d18 = d15;
            double d19 = (double) f4;
            Double.isNaN(d19);
            double d20 = d17 + (d19 * sin);
            Double.isNaN(d8);
            double d21 = d20 / d8;
            double d22 = d8;
            double d23 = (double) (-f9);
            Double.isNaN(d23);
            double d24 = d23 * sin;
            Double.isNaN(d19);
            double d25 = d24 + (d19 * cos);
            Double.isNaN(d14);
            double d26 = d25 / d14;
            double d27 = d9 - d21;
            double d28 = d18 - d26;
            double d29 = (d9 + d21) / 2.0d;
            double d30 = (d18 + d26) / 2.0d;
            double d31 = sin;
            double d32 = (d27 * d27) + (d28 * d28);
            if (d32 == 0.0d) {
                Log.w(PathParser.LOGTAG, " Points are coincident");
                return;
            }
            double d33 = (1.0d / d32) - 0.25d;
            if (d33 < 0.0d) {
                String str = PathParser.LOGTAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Points are too far apart ");
                sb.append(d32);
                Log.w(str, sb.toString());
                float sqrt = (float) (Math.sqrt(d32) / 1.99999d);
                drawArc(path, f, f2, f3, f4, f10 * sqrt, f6 * sqrt, f7, z, z2);
                return;
            }
            double sqrt2 = Math.sqrt(d33);
            double d34 = d27 * sqrt2;
            double d35 = sqrt2 * d28;
            boolean z4 = z2;
            if (z == z4) {
                d2 = d29 - d35;
                d = d30 + d34;
            } else {
                d2 = d29 + d35;
                d = d30 - d34;
            }
            double atan2 = Math.atan2(d18 - d, d9 - d2);
            double atan22 = Math.atan2(d26 - d, d21 - d2) - atan2;
            if (z4 != (atan22 >= 0.0d)) {
                atan22 = atan22 > 0.0d ? atan22 - 6.283185307179586d : atan22 + 6.283185307179586d;
            }
            double d36 = atan22;
            Double.isNaN(d22);
            double d37 = d2 * d22;
            Double.isNaN(d14);
            double d38 = d * d14;
            arcToBezier(path, (d37 * cos) - (d38 * d31), (d37 * d31) + (d38 * cos), d22, d14, d5, d13, radians, atan2, d36);
        }

        private static void arcToBezier(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
            double d10 = d3;
            int ceil = (int) Math.ceil(Math.abs((d9 * 4.0d) / 3.141592653589793d));
            double cos = Math.cos(d7);
            double sin = Math.sin(d7);
            double cos2 = Math.cos(d8);
            double sin2 = Math.sin(d8);
            double d11 = -d10;
            double d12 = d11 * cos;
            double d13 = d4 * sin;
            double d14 = (d12 * sin2) - (d13 * cos2);
            double d15 = d11 * sin;
            double d16 = d4 * cos;
            double d17 = (sin2 * d15) + (cos2 * d16);
            double d18 = (double) ceil;
            Double.isNaN(d18);
            double d19 = d9 / d18;
            int i = 0;
            double d20 = d6;
            double d21 = d17;
            double d22 = d14;
            double d23 = d5;
            double d24 = d8;
            while (i < ceil) {
                double d25 = d24 + d19;
                double sin3 = Math.sin(d25);
                double cos3 = Math.cos(d25);
                double d26 = d19;
                double d27 = (d + ((d10 * cos) * cos3)) - (d13 * sin3);
                double d28 = d2 + (d10 * sin * cos3) + (d16 * sin3);
                double d29 = (d12 * sin3) - (d13 * cos3);
                double d30 = (sin3 * d15) + (cos3 * d16);
                double d31 = d25 - d24;
                double tan = Math.tan(d31 / 2.0d);
                double sin4 = (Math.sin(d31) * (Math.sqrt(((tan * 3.0d) * tan) + 4.0d) - 1.0d)) / 3.0d;
                double d32 = d16;
                double d33 = d23 + (d22 * sin4);
                double d34 = d15;
                double d35 = d20 + (d21 * sin4);
                int i2 = ceil;
                double d36 = cos;
                double d37 = d27 - (sin4 * d29);
                double d38 = d28 - (sin4 * d30);
                double d39 = sin;
                path.rLineTo(0.0f, 0.0f);
                path.cubicTo((float) d33, (float) d35, (float) d37, (float) d38, (float) d27, (float) d28);
                i++;
                d20 = d28;
                d23 = d27;
                d24 = d25;
                d21 = d30;
                d22 = d29;
                d19 = d26;
                d16 = d32;
                d15 = d34;
                ceil = i2;
                cos = d36;
                sin = d39;
                d10 = d3;
            }
        }
    }

    PathParser() {
    }

    static float[] copyOfRange(float[] fArr, int i, int i2) {
        if (i <= i2) {
            int length = fArr.length;
            if (i < 0 || i > length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int i3 = i2 - i;
            int min = Math.min(i3, length - i);
            float[] fArr2 = new float[i3];
            System.arraycopy(fArr, i, fArr2, 0, min);
            return fArr2;
        }
        throw new IllegalArgumentException();
    }

    public static Path createPathFromPathData(String str) {
        Path path = new Path();
        PathDataNode[] createNodesFromPathData = createNodesFromPathData(str);
        if (createNodesFromPathData == null) {
            return null;
        }
        try {
            PathDataNode.nodesToPath(createNodesFromPathData, path);
            return path;
        } catch (RuntimeException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error in parsing ");
            sb.append(str);
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public static PathDataNode[] createNodesFromPathData(String str) {
        if (str == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = 1;
        int i2 = 0;
        while (i < str.length()) {
            int nextStart = nextStart(str, i);
            String trim = str.substring(i2, nextStart).trim();
            if (trim.length() > 0) {
                addNode(arrayList, trim.charAt(0), getFloats(trim));
            }
            i2 = nextStart;
            i = nextStart + 1;
        }
        if (i - i2 == 1 && i2 < str.length()) {
            addNode(arrayList, str.charAt(i2), new float[0]);
        }
        return (PathDataNode[]) arrayList.toArray(new PathDataNode[arrayList.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] pathDataNodeArr) {
        if (pathDataNodeArr == null) {
            return null;
        }
        PathDataNode[] pathDataNodeArr2 = new PathDataNode[pathDataNodeArr.length];
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            pathDataNodeArr2[i] = new PathDataNode(pathDataNodeArr[i]);
        }
        return pathDataNodeArr2;
    }

    public static boolean canMorph(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        if (pathDataNodeArr == null || pathDataNodeArr2 == null || pathDataNodeArr.length != pathDataNodeArr2.length) {
            return false;
        }
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            if (pathDataNodeArr[i].type != pathDataNodeArr2[i].type || pathDataNodeArr[i].params.length != pathDataNodeArr2[i].params.length) {
                return false;
            }
        }
        return true;
    }

    public static void updateNodes(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        for (int i = 0; i < pathDataNodeArr2.length; i++) {
            pathDataNodeArr[i].type = pathDataNodeArr2[i].type;
            for (int i2 = 0; i2 < pathDataNodeArr2[i].params.length; i2++) {
                pathDataNodeArr[i].params[i2] = pathDataNodeArr2[i].params[i2];
            }
        }
    }

    private static int nextStart(String str, int i) {
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (((charAt - 'A') * (charAt - 'Z') <= 0 || (charAt - 'a') * (charAt - 'z') <= 0) && charAt != 'e' && charAt != 'E') {
                return i;
            }
            i++;
        }
        return i;
    }

    private static void addNode(ArrayList<PathDataNode> arrayList, char c, float[] fArr) {
        arrayList.add(new PathDataNode(c, fArr));
    }

    private static float[] getFloats(String str) {
        int i = 1;
        if ((str.charAt(0) == 'z') || (str.charAt(0) == 'Z')) {
            return new float[0];
        }
        try {
            float[] fArr = new float[str.length()];
            ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            int length = str.length();
            int i2 = 0;
            while (i < length) {
                extract(str, i, extractFloatResult);
                int i3 = extractFloatResult.mEndPosition;
                if (i < i3) {
                    int i4 = i2 + 1;
                    fArr[i2] = Float.parseFloat(str.substring(i, i3));
                    i2 = i4;
                }
                i = extractFloatResult.mEndWithNegOrDot ? i3 : i3 + 1;
            }
            return copyOfRange(fArr, 0, i2);
        } catch (NumberFormatException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("error in parsing \"");
            sb.append(str);
            sb.append("\"");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        r2 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x003a A[LOOP:0: B:1:0x0007->B:20:0x003a, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x003d A[SYNTHETIC] */
    private static void extract(String str, int i, ExtractFloatResult extractFloatResult) {
        extractFloatResult.mEndWithNegOrDot = false;
        int i2 = i;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        while (i2 < str.length()) {
            char charAt = str.charAt(i2);
            if (charAt != ' ') {
                if (charAt == 'E' || charAt == 'e') {
                    z = true;
                    if (!z3) {
                        extractFloatResult.mEndPosition = i2;
                    }
                    i2++;
                } else {
                    switch (charAt) {
                        case ',':
                            break;
                        case '-':
                            if (i2 != i && !z) {
                                extractFloatResult.mEndWithNegOrDot = true;
                            }
                        case '.':
                            if (!z2) {
                                z = false;
                                z2 = true;
                                break;
                            } else {
                                extractFloatResult.mEndWithNegOrDot = true;
                            }
                    }
                }
            }
            z = false;
            z3 = true;
            if (!z3) {
            }
        }
        extractFloatResult.mEndPosition = i2;
    }
}
