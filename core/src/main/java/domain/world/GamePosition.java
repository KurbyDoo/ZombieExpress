//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package domain.world;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class GamePosition implements Serializable {
    public static final GamePosition X = new GamePosition(1.0F, 0.0F, 0.0F);
    public static final GamePosition Y = new GamePosition(0.0F, 1.0F, 0.0F);
    public static final GamePosition Z = new GamePosition(0.0F, 0.0F, 1.0F);
    public static final GamePosition Zero = new GamePosition(0.0F, 0.0F, 0.0F);
    private static final long serialVersionUID = 3840054589595372522L;
    private static final Matrix4 tmpMat = new Matrix4();
    public float x;
    public float y;
    public float z;

    public GamePosition() {
    }

    public GamePosition(float x, float y, float z) {
        this.set(x, y, z);
    }

    public GamePosition(GamePosition vector) {
        this.set(vector);
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

    public GamePosition scl(float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public float len() {
        return (float) Math.sqrt((double) (this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public float dst(GamePosition vector) {
        float a = vector.x - this.x;
        float b = vector.y - this.y;
        float c = vector.z - this.z;
        return (float) Math.sqrt((double) (a * a + b * b + c * c));
    }


    public GamePosition nor() {
        float len2 = this.len2();
        return len2 != 0.0F && len2 != 1.0F ? this.scl(1.0F / (float) Math.sqrt((double) len2)) : this;
    }

    public float dot(GamePosition vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }

    public GamePosition crs(GamePosition vector) {
        return this.set(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z,
            this.x * vector.y - this.y * vector.x);
    }

    public GamePosition mul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12],
            this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13],
            this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]);
    }

    public GamePosition rotate(GamePosition axis, float degrees) {
        tmpMat.setToRotation(axis.x, axis.y, axis.z, degrees);
        return this.mul(tmpMat);
    }

    public boolean isZero() {
        return this.x == 0.0F && this.y == 0.0F && this.z == 0.0F;
    }

    public GamePosition lerp(GamePosition target, float alpha) {
        this.x += alpha * (target.x - this.x);
        this.y += alpha * (target.y - this.y);
        this.z += alpha * (target.z - this.z);
        return this;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
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
            GamePosition other = (GamePosition) obj;
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
}
