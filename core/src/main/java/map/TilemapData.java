package map;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TilemapData {
    public int tileSize;
    public int mapWidth;
    public int mapHeight;
    public Array<Layer> layers;

    public static class Layer {
        public String name;
        public Array<Tile> tiles;
        public boolean collider;
    }

    public static class Tile {
        public String id; // The ID of the sprite in your spritesheet
        public int x;     // Grid X position
        public int y;     // Grid Y position
        public Attributes attributes;

        public Rectangle bounds;
        public void populateBounds (int tileSize, int mapHeight) {
            bounds = new Rectangle(x * tileSize, (mapHeight - y - 1) * tileSize, tileSize, tileSize);
        }

        public void renderDebug(ShapeRenderer shapeRenderer) {
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public static class Attributes {
        public String enemyType;
    }
}
