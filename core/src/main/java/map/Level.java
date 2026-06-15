package map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import entities.Entity;


public class Level {
    //<editor-fold desc="Class Vars">
    private static final Json json = new Json();
    private static final TilemapData tilemap = json.fromJson(TilemapData.class, Gdx.files.internal("data/map.json"));
    private static TilemapData.Layer EnemyData = null;

    private static TextureRegion[][] tiles;
    private static int widthInTiles;
    private static int tileSize;
    //</editor-fold>

    public Level() {
        for (TilemapData.Layer layer : tilemap.layers) {
            if (layer.name.equals("Enemies")) {
                EnemyData = layer;
                continue;
            }
            for (TilemapData.Tile tile : layer.tiles) {
                tile.populateBounds(tilemap.tileSize, tilemap.mapHeight);
            }
        }

        Texture tileset = new Texture(Gdx.files.internal("data/spritesheet.png"));
        tiles = TextureRegion.split(tileset, tilemap.tileSize, tilemap.tileSize);
        widthInTiles = tileset.getWidth() / tilemap.tileSize;
        tileSize = tilemap.tileSize;
        tilemap.layers.reverse();
    }

    public void render(SpriteBatch batch) {
        for (TilemapData.Layer layer : tilemap.layers) {
            if (layer.name.equals("Enemies")) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileId = Integer.parseInt(tile.id);
                int tileRow = tileId / widthInTiles;
                int tileCol = tileId % widthInTiles;
                batch.draw(tiles[tileRow][tileCol], tile.x * tileSize, (tilemap.mapHeight - tile.y - 1) * tileSize);
            }
        }
    }

    public void renderDebug (ShapeRenderer shapeRenderer) {
        for (TilemapData.Layer layer : tilemap.layers) {
            if (layer.name.equals("Enemies")) continue;
            shapeRenderer.setColor(layer.collider ? Color.BLUE : Color.RED);
            for (TilemapData.Tile tile : layer.tiles) {
                tile.renderDebug(shapeRenderer);
            }
        }
    }

    public static TilemapData getTilemapData() {
        return tilemap;
    }

    public static TilemapData.Layer getEnemyData() {
        return EnemyData;
    }

    //<editor-fold desc="Colliders">
    @FunctionalInterface
    private interface TileAction {
        boolean run(TilemapData.Tile tile, int tileXPos, int tileYPos);
    }

    private static void checkNearbyCollisions(Entity entity, TileAction action) {
        float entityX = entity.getBounds().x;
        float entityY = entity.getBounds().y;
        int bound = tileSize * 2;

        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            if (layer.name.equals("Enemies")) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = tile.x * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;

                if (tileXPos > entityX - bound && tileXPos < entityX + bound &&
                    tileYPos > entityY - bound && tileYPos < entityY + bound) {
                    if (action.run(tile, tileXPos, tileYPos)) return;
                }
            }
        }
    }

    public static void checkHorizontalCollisions(Entity entity) {
        checkNearbyCollisions(entity, (tile, tileXPos, tileYPos) -> {
            if (tile.bounds.overlaps(entity.getBounds())) {
                // Moving Right -> Hit left wall of tile
                if (entity.getVelX() > 0) {
                    float newX = tileXPos - entity.getBounds().width;
                    entity.setXPosition(newX);
                }
                // Moving Left -> Hit right wall of tile
                else if (entity.getVelX() < 0) {
                    float newX = tileXPos + tileSize;
                    entity.setXPosition(newX);
                }
                entity.setVelX(0); // Stop horizontal momentum
                return true; // Stop searching (simulates the old break loop)
            }
            return false;
        });
    }

    public static void checkVerticalCollisions(Entity entity) {
        checkNearbyCollisions(entity, (tile, tileXPos, tileYPos) -> {
            if (tile.bounds.overlaps(entity.getBounds())) {
                // Falling Down -> Land on top of tile
                if (entity.getVelY() < 0) {
                    float newY = tileYPos + tileSize;
                    entity.setYPosition(newY);
                    entity.setIsGrounded(true);
                }
                // Jumping Up -> Hit ceiling (bottom of tile)
                else if (entity.getVelY() > 0) {
                    float newY = tileYPos - entity.getBounds().height;
                    entity.setYPosition(newY);
                }
                entity.setVelY(0); // Stop vertical momentum
                return true; // Stop searching (simulates the old break loop)
            }
            return false;
        });
    }

    public static boolean isStandingOnGround(Entity entity) {
        final boolean[] result = {false};
        checkNearbyCollisions(entity, (tile, tileXPos, tileYPos) -> {
            if (entity.getFeet().overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }

    public static boolean wouldXCollide(Entity entity) {
        final boolean[] result = {false};
        checkNearbyCollisions(entity, (tile, tileXPos, tileYPos) -> {
            if (entity.getNextXBounds().overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }

    public static boolean wouldYCollide(Entity entity) {
        final boolean[] result = {false};
        checkNearbyCollisions(entity, (tile, tileXPos, tileYPos) -> {
            if (entity.getNextYBounds().overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }
    //</editor-fold>
}
