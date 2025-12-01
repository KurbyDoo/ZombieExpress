/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Value Object Pattern: Represents a 3D position/vector.
 * - Fluent Interface Pattern: Methods return 'this' for method chaining.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [CRITICAL VIOLATION] This domain class imports LibGDX framework classes:
 *   - com.badlogic.gdx.math.MathUtils
 *   - com.badlogic.gdx.math.Matrix3
 *   - com.badlogic.gdx.math.Matrix4
 *   - com.badlogic.gdx.utils.GdxRuntimeException
 *   - com.badlogic.gdx.utils.NumberUtils
 *
 *   According to Clean Architecture (Chapter 11), domain layer entities MUST NOT
 *   depend on framework-specific code. This creates a tight coupling between the
 *   domain model and LibGDX, making it impossible to test or use the domain
 *   independently of the framework.
 *
 * RECOMMENDED FIX:
 *   - Create a pure Java implementation using java.lang.Math.
 *   - Move matrix operations to an infrastructure adapter.
 *   - Replace GdxRuntimeException with IllegalArgumentException.
 *   - Replace MathUtils with java.lang.Math equivalents.
 *   - The file appears to be decompiled from a .class file, which suggests this
 *     may have been auto-generated. A clean rewrite is recommended.
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Class has many responsibilities (position, vector math, matrix ops).
 *   Consider splitting into Position and VectorMath utility classes.
 * - [PASS] OCP: Methods are well-organized for extension.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [FAIL] DIP: Depends on concrete LibGDX classes instead of abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [WARN] Public fields (x, y, z) break encapsulation. Should be private with getters.
 * - [WARN] Static mutable field 'tmpMat' is not thread-safe.
 * - [MINOR] Missing Javadoc documentation.
 * - [MINOR] Float literals use 'F' suffix inconsistently.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Line lengths may exceed standard 100/120 character limit.
 * - [WARN] Missing Javadoc for all public methods.
 * - [WARN] Public fields violate encapsulation rules.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package domain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class GamePosition implements Serializable {
    private static final long serialVersionUID = 3840054589595372522L;
    public float x;
    public float y;
    public float z;
    public static final GamePosition X = new GamePosition(1.0F, 0.0F, 0.0F);
    public static final GamePosition Y = new GamePosition(0.0F, 1.0F, 0.0F);
    public static final GamePosition Z = new GamePosition(0.0F, 0.0F, 1.0F);
    public static final GamePosition Zero = new GamePosition(0.0F, 0.0F, 0.0F);
    private static final Matrix4 tmpMat = new Matrix4();

    public GamePosition() {
    }

    public GamePosition(float x, float y, float z) {
        this.set(x, y, z);
    }

    public GamePosition(GamePosition vector) {
        this.set(vector);
    }

    public GamePosition(float[] values) {
        this.set(values[0], values[1], values[2]);
    }

    public GamePosition set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public GamePosition set(GamePosition vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    public GamePosition set(float[] values) {
        return this.set(values[0], values[1], values[2]);
    }


    public GamePosition setFromSpherical(float azimuthalAngle, float polarAngle) {
        float cosPolar = MathUtils.cos(polarAngle);
        float sinPolar = MathUtils.sin(polarAngle);
        float cosAzim = MathUtils.cos(azimuthalAngle);
        float sinAzim = MathUtils.sin(azimuthalAngle);
        return this.set(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
    }

    public GamePosition setToRandomDirection() {
        float u = MathUtils.random();
        float v = MathUtils.random();
        float theta = 6.2831855F * u;
        float phi = (float)Math.acos((double)(2.0F * v - 1.0F));
        return this.setFromSpherical(theta, phi);
    }

    public GamePosition cpy() {
        return new GamePosition(this);
    }

    public GamePosition add(GamePosition vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public GamePosition add(float x, float y, float z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    public GamePosition add(float values) {
        return this.set(this.x + values, this.y + values, this.z + values);
    }

    public GamePosition sub(GamePosition a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    public GamePosition sub(float x, float y, float z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    public GamePosition sub(float value) {
        return this.set(this.x - value, this.y - value, this.z - value);
    }

    public GamePosition scl(float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public GamePosition scl(GamePosition other) {
        return this.set(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public GamePosition scl(float vx, float vy, float vz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz);
    }

    public GamePosition mulAdd(GamePosition vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        return this;
    }

    public GamePosition mulAdd(GamePosition vec, GamePosition mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        return this;
    }

    public static float len(float x, float y, float z) {
        return (float)Math.sqrt((double)(x * x + y * y + z * z));
    }

    public float len() {
        return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public static float len2(float x, float y, float z) {
        return x * x + y * y + z * z;
    }

    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public boolean idt(GamePosition vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }

    public static float dst(float x1, float y1, float z1, float x2, float y2, float z2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        return (float)Math.sqrt((double)(a * a + b * b + c * c));
    }

    public float dst(GamePosition vector) {
        float a = vector.x - this.x;
        float b = vector.y - this.y;
        float c = vector.z - this.z;
        return (float)Math.sqrt((double)(a * a + b * b + c * c));
    }

    public float dst(float x, float y, float z) {
        float a = x - this.x;
        float b = y - this.y;
        float c = z - this.z;
        return (float)Math.sqrt((double)(a * a + b * b + c * c));
    }

    public static float dst2(float x1, float y1, float z1, float x2, float y2, float z2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        return a * a + b * b + c * c;
    }

    public float dst2(GamePosition point) {
        float a = point.x - this.x;
        float b = point.y - this.y;
        float c = point.z - this.z;
        return a * a + b * b + c * c;
    }

    public float dst2(float x, float y, float z) {
        float a = x - this.x;
        float b = y - this.y;
        float c = z - this.z;
        return a * a + b * b + c * c;
    }

    public GamePosition nor() {
        float len2 = this.len2();
        return len2 != 0.0F && len2 != 1.0F ? this.scl(1.0F / (float)Math.sqrt((double)len2)) : this;
    }

    public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public float dot(GamePosition vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }

    public float dot(float x, float y, float z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public GamePosition crs(GamePosition vector) {
        return this.set(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z, this.x * vector.y - this.y * vector.x);
    }

    public GamePosition crs(float x, float y, float z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    public GamePosition mul4x3(float[] matrix) {
        return this.set(this.x * matrix[0] + this.y * matrix[3] + this.z * matrix[6] + matrix[9], this.x * matrix[1] + this.y * matrix[4] + this.z * matrix[7] + matrix[10], this.x * matrix[2] + this.y * matrix[5] + this.z * matrix[8] + matrix[11]);
    }

    public GamePosition mul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]);
    }

    public GamePosition traMul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2] + l_mat[3], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6] + l_mat[7], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10] + l_mat[11]);
    }

    public GamePosition mul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[3] + this.z * l_mat[6], this.x * l_mat[1] + this.y * l_mat[4] + this.z * l_mat[7], this.x * l_mat[2] + this.y * l_mat[5] + this.z * l_mat[8]);
    }

    public GamePosition traMul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[3] + this.y * l_mat[4] + this.z * l_mat[5], this.x * l_mat[6] + this.y * l_mat[7] + this.z * l_mat[8]);
    }

    public GamePosition prj(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float l_w = 1.0F / (this.x * l_mat[3] + this.y * l_mat[7] + this.z * l_mat[11] + l_mat[15]);
        return this.set((this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12]) * l_w, (this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13]) * l_w, (this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]) * l_w);
    }

    public GamePosition rot(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10]);
    }

    public GamePosition unrotate(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }

    public GamePosition untransform(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        this.x -= l_mat[12];
        this.y -= l_mat[12];
        this.z -= l_mat[12];
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }

    public GamePosition rotate(float degrees, float axisX, float axisY, float axisZ) {
        return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
    }

    public GamePosition rotateRad(float radians, float axisX, float axisY, float axisZ) {
        return this.mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
    }

    public GamePosition rotate(GamePosition axis, float degrees) {
        tmpMat.setToRotation(axis.x, axis.y, axis.z, degrees);
        return this.mul(tmpMat);
    }

    public GamePosition rotateRad(GamePosition axis, float radians) {
        tmpMat.setToRotationRad(axis.x, axis.y, axis.z, radians);
        return this.mul(tmpMat);
    }

    public boolean isUnit() {
        return this.isUnit(1.0E-9F);
    }

    public boolean isUnit(float margin) {
        return Math.abs(this.len2() - 1.0F) < margin;
    }

    public boolean isZero() {
        return this.x == 0.0F && this.y == 0.0F && this.z == 0.0F;
    }

    public boolean isZero(float margin) {
        return this.len2() < margin;
    }

    public boolean isOnLine(GamePosition other, float epsilon) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= epsilon;
    }

    public boolean isOnLine(GamePosition other) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= 1.0E-6F;
    }

    public boolean isCollinear(GamePosition other, float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasSameDirection(other);
    }

    public boolean isCollinear(GamePosition other) {
        return this.isOnLine(other) && this.hasSameDirection(other);
    }

    public boolean isCollinearOpposite(GamePosition other, float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasOppositeDirection(other);
    }

    public boolean isCollinearOpposite(GamePosition other) {
        return this.isOnLine(other) && this.hasOppositeDirection(other);
    }

    public boolean isPerpendicular(GamePosition vector) {
        return MathUtils.isZero(this.dot(vector));
    }

    public boolean isPerpendicular(GamePosition vector, float epsilon) {
        return MathUtils.isZero(this.dot(vector), epsilon);
    }

    public boolean hasSameDirection(GamePosition vector) {
        return this.dot(vector) > 0.0F;
    }

    public boolean hasOppositeDirection(GamePosition vector) {
        return this.dot(vector) < 0.0F;
    }

    public GamePosition lerp(GamePosition target, float alpha) {
        this.x += alpha * (target.x - this.x);
        this.y += alpha * (target.y - this.y);
        this.z += alpha * (target.z - this.z);
        return this;
    }

    public GamePosition slerp(GamePosition target, float alpha) {
        float dot = this.dot(target);
        if (!((double)dot > 0.9995) && !((double)dot < -0.9995)) {
            float theta0 = (float)Math.acos((double)dot);
            float theta = theta0 * alpha;
            float st = (float)Math.sin((double)theta);
            float tx = target.x - this.x * dot;
            float ty = target.y - this.y * dot;
            float tz = target.z - this.z * dot;
            float l2 = tx * tx + ty * ty + tz * tz;
            float dl = st * (l2 < 1.0E-4F ? 1.0F : 1.0F / (float)Math.sqrt((double)l2));
            return this.scl((float)Math.cos((double)theta)).add(tx * dl, ty * dl, tz * dl).nor();
        } else {
            return this.lerp(target, alpha);
        }
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    public GamePosition fromString(String v) {
        int s0 = v.indexOf(44, 1);
        int s1 = v.indexOf(44, s0 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                float x = Float.parseFloat(v.substring(1, s0));
                float y = Float.parseFloat(v.substring(s0 + 1, s1));
                float z = Float.parseFloat(v.substring(s1 + 1, v.length() - 1));
                return this.set(x, y, z);
            } catch (NumberFormatException var7) {
            }
        }

        throw new GdxRuntimeException("Malformed GamePosition: " + v);
    }

    public GamePosition limit(float limit) {
        return this.limit2(limit * limit);
    }

    public GamePosition limit2(float limit2) {
        float len2 = this.len2();
        if (len2 > limit2) {
            this.scl((float)Math.sqrt((double)(limit2 / len2)));
        }

        return this;
    }

    public GamePosition setLength(float len) {
        return this.setLength2(len * len);
    }

    public GamePosition setLength2(float len2) {
        float oldLen2 = this.len2();
        return oldLen2 != 0.0F && oldLen2 != len2 ? this.scl((float)Math.sqrt((double)(len2 / oldLen2))) : this;
    }

    public GamePosition clamp(float min, float max) {
        float len2 = this.len2();
        if (len2 == 0.0F) {
            return this;
        } else {
            float max2 = max * max;
            if (len2 > max2) {
                return this.scl((float)Math.sqrt((double)(max2 / len2)));
            } else {
                float min2 = min * min;
                return len2 < min2 ? this.scl((float)Math.sqrt((double)(min2 / len2))) : this;
            }
        }
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + NumberUtils.floatToIntBits(this.x);
        result = 31 * result + NumberUtils.floatToIntBits(this.y);
        result = 31 * result + NumberUtils.floatToIntBits(this.z);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            GamePosition other = (GamePosition)obj;
            if (NumberUtils.floatToIntBits(this.x) != NumberUtils.floatToIntBits(other.x)) {
                return false;
            } else if (NumberUtils.floatToIntBits(this.y) != NumberUtils.floatToIntBits(other.y)) {
                return false;
            } else {
                return NumberUtils.floatToIntBits(this.z) == NumberUtils.floatToIntBits(other.z);
            }
        }
    }

    public boolean epsilonEquals(GamePosition other, float epsilon) {
        if (other == null) {
            return false;
        } else if (Math.abs(other.x - this.x) > epsilon) {
            return false;
        } else if (Math.abs(other.y - this.y) > epsilon) {
            return false;
        } else {
            return !(Math.abs(other.z - this.z) > epsilon);
        }
    }

    public boolean epsilonEquals(float x, float y, float z, float epsilon) {
        if (Math.abs(x - this.x) > epsilon) {
            return false;
        } else if (Math.abs(y - this.y) > epsilon) {
            return false;
        } else {
            return !(Math.abs(z - this.z) > epsilon);
        }
    }

    public boolean epsilonEquals(GamePosition other) {
        return this.epsilonEquals(other, 1.0E-6F);
    }

    public boolean epsilonEquals(float x, float y, float z) {
        return this.epsilonEquals(x, y, z, 1.0E-6F);
    }

    public GamePosition setZero() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        return this;
    }
}
