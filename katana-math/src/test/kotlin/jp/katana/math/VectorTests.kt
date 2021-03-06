package jp.katana.math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VectorTests {
    @Test
    fun equalTests() {
        val x = 10.5
        val y = 5.5
        val z = 20.5

        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) == Vector2(x, y)
        )
        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) != Vector2(y, x)
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) == Vector3(x, y, z)
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) != Vector3(y, y, z)
        )
        Assertions.assertTrue(
            Vector2Int(
                x.toInt(),
                y.toInt()
            ) == Vector2Int(
                x.toInt(),
                y.toInt()
            )
        )
        Assertions.assertTrue(
            Vector2Int(
                x.toInt(),
                y.toInt()
            ) != Vector2Int(
                y.toInt(),
                y.toInt()
            )
        )
        Assertions.assertTrue(
            Vector3Int(
                x.toInt(),
                y.toInt(),
                z.toInt()
            ) == Vector3Int(
                x.toInt(),
                y.toInt(),
                z.toInt()
            )
        )
        Assertions.assertTrue(
            Vector3Int(
                x.toInt(),
                y.toInt(),
                z.toInt()
            ) != Vector3Int(
                y.toInt(),
                y.toInt(),
                z.toInt()
            )
        )
    }

    @Test
    fun operateTests() {
        val x = 10.5
        val y = 5.5
        val z = 20.5

        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) + Vector2(x, y) == Vector2(
                x + x,
                y + y
            )
        )
        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) - Vector2(x, y) == Vector2(
                x - x,
                y - y
            )
        )
        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) / 2.0 == Vector2(x / 2.0, y / 2.0)
        )
        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) * 2.0 == Vector2(x * 2.0, y * 2.0)
        )
        Assertions.assertTrue(
            Vector2(
                x,
                y
            ) % 2.0 == Vector2(x % 2.0, y % 2.0)
        )

        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) + Vector3(x, y, z) == Vector3(
                x + x,
                y + y,
                z + z
            )
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) - Vector3(x, y, z) == Vector3(
                x - x,
                y - y,
                z - z
            )
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) / 2.0 == Vector3(x / 2.0, y / 2.0, z / 2.0)
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) * 2.0 == Vector3(x * 2.0, y * 2.0, z * 2.0)
        )
        Assertions.assertTrue(
            Vector3(
                x,
                y,
                z
            ) % 2.0 == Vector3(x % 2.0, y % 2.0, z % 2.0)
        )
    }

    @Test
    fun convertTests() {
        val x = 10.5
        val y = 5.5
        val z = 20.5

        Assertions.assertTrue(
            Vector2(x, y)
                .toVector2Int() == Vector2Int(x.toInt(), y.toInt())
        )
        Assertions.assertTrue(
            Vector2(x, y)
                .toVector3XY() == Vector3(x, y, 0.0)
        )
        Assertions.assertTrue(
            Vector2(x, y)
                .toVector3XZ() == Vector3(x, 0.0, y)
        )

        Assertions.assertTrue(
            Vector3(x, y, z)
                .toVector3Int() == Vector3Int(x.toInt(), y.toInt(), z.toInt())
        )
        Assertions.assertTrue(
            Vector3(x, y, z)
                .toVector2XY() == Vector2(x, y)
        )
        Assertions.assertTrue(
            Vector3(x, y, z)
                .toVector2XZ() == Vector2(x, z)
        )
    }
}