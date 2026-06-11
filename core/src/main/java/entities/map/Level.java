package entities.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import entities.Player;

public class Level {
    //<editor-fold desc="Class Vars">
    private static final Json json = new Json();
    private static final TilemapData tilemap = json.fromJson(TilemapData.class, Gdx.files.internal("data/map.json"));
    private static TextureRegion[][] tiles;
    private static int widthInTiles;
    private static int tileSize;
    private static Player player;
    //</editor-fold>

    public Level(Player player) {
        for (TilemapData.Layer layer : tilemap.layers) {
            for (TilemapData.Tile tile : layer.tiles) {
                tile.populateBounds(tilemap.tileSize, tilemap.mapHeight);
            }
        }

        Level.player = player;
        Texture tileset = new Texture(Gdx.files.internal("data/spritesheet.png"));
        tiles = TextureRegion.split(tileset, tilemap.tileSize, tilemap.tileSize);
        widthInTiles = tileset.getWidth() / tilemap.tileSize;
        tileSize = tilemap.tileSize;
    }

    public void render(SpriteBatch batch) {
        for (TilemapData.Layer layer : tilemap.layers) {
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
            shapeRenderer.setColor(layer.collider ? Color.BLUE : Color.RED);
            for (TilemapData.Tile tile : layer.tiles) {
                tile.renderDebug(shapeRenderer);
            }
        }
    }

    public static TilemapData getTilemapData() {
        return tilemap;
    }

    //<editor-fold desc="Colliders">
    @FunctionalInterface
    private interface TileAction {
        boolean run(TilemapData.Tile tile, int tileXPos, int tileYPos);
    }

    private static void checkNearbyCollisions(TileAction action) {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = tile.x * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;

                // Broad-phase proximity check
                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

                    // Run the custom block. If it returns true, stop checking tiles.
                    if (action.run(tile, tileXPos, tileYPos)) {
                        return;
                    }
                }
            }
        }
    }

    public static void checkHorizontalCollisions() {
        checkNearbyCollisions((tile, tileXPos, tileYPos) -> {
            if (tile.bounds.overlaps(Player.bounds)) {
                // Moving Right -> Hit left wall of tile
                if (player.getVelX() > 0) {
                    float newX = tileXPos - Player.bounds.width;
                    player.setXPosition(newX);
                }
                // Moving Left -> Hit right wall of tile
                else if (player.getVelX() < 0) {
                    float newX = tileXPos + tileSize;
                    player.setXPosition(newX);
                }
                player.setVelX(0); // Stop horizontal momentum
                return true; // Stop searching (simulates the old break loop)
            }
            return false;
        });
    }

    public static void checkVerticalCollisions() {
        checkNearbyCollisions((tile, tileXPos, tileYPos) -> {
            if (tile.bounds.overlaps(Player.bounds)) {
                // Falling Down -> Land on top of tile
                if (player.getVelY() < 0) {
                    float newY = tileYPos + tileSize;
                    player.setYPosition(newY);
                    player.setIsGrounded(true);
                }
                // Jumping Up -> Hit ceiling (bottom of tile)
                else if (player.getVelY() > 0) {
                    float newY = tileYPos - Player.bounds.height;
                    player.setYPosition(newY);
                }
                player.setVelY(0); // Stop vertical momentum
                return true; // Stop searching (simulates the old break loop)
            }
            return false;
        });
    }

    public static boolean isStandingOnGround() {
        final boolean[] result = {false};
        checkNearbyCollisions((tile, tileXPos, tileYPos) -> {
            if (Player.feet.overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }

    public static boolean wouldXCollide() {
        final boolean[] result = {false};
        checkNearbyCollisions((tile, tileXPos, tileYPos) -> {
            if (Player.nextXBounds.overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }

    public static boolean wouldYCollide() {
        final boolean[] result = {false};
        checkNearbyCollisions((tile, tileXPos, tileYPos) -> {
            if (Player.nextYBounds.overlaps(tile.bounds)) {
                result[0] = true;
                return true; // Stop searching early
            }
            return false;
        });
        return result[0];
    }
    //</editor-fold>
}
