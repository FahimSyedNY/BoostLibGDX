package entities.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import entities.Player;

public class Level {
    private static final Json json = new Json();
    private static final TilemapData tilemap = json.fromJson(TilemapData.class, Gdx.files.internal("data/map.json"));;
    private static TextureRegion[][] tiles;
    private static int widthInTiles;
    private static int tileSize;
    private static Player player;

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

    public static TilemapData getTilemapData() {
        return tilemap;
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
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;
        for (TilemapData.Layer layer : tilemap.layers) {
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = tile.x * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;

//                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
//                    tileYPos > playerY - bound && tileYPos < playerY + bound) {
                    tile.renderDebug(shapeRenderer);
//                }
            }
        }
    }

    public static void checkHorizontalCollisions() {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        horizontalLoop:
        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = (tile.x) * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;

                // Broad-phase proximity check
                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

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
                        break horizontalLoop; // Exit both loops immediately
                    }
                }
            }
        }
    }

    public static void checkVerticalCollisions() {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        verticalLoop:
        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = (tile.x) * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;

                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

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
                        break verticalLoop; // Exit both loops immediately
                    }
                }
            }
        }
    }

    public static boolean isStandingOnGround() {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = (tile.x) * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;
                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

                    if (Player.feet.overlaps(tile.bounds)) return true;
                }
            }
        }
        return false;
    }

    public static boolean wouldXCollide() {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = (tile.x) * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;
                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

                    if (Player.nextXBounds.overlaps(tile.bounds)) return true;
                }
            }
        }
        return false;
    }

    public static boolean wouldYCollide() {
        float playerX = Player.bounds.x;
        float playerY = Player.bounds.y;
        int bound = tileSize * 2;

        for (TilemapData.Layer layer : tilemap.layers) {
            if (!layer.collider) continue;
            for (TilemapData.Tile tile : layer.tiles) {
                int tileXPos = (tile.x) * tileSize;
                int tileYPos = (tilemap.mapHeight - tile.y - 1) * tileSize;
                if (tileXPos > playerX - bound && tileXPos < playerX + bound &&
                    tileYPos > playerY - bound && tileYPos < playerY + bound) {

                    if (Player.nextYBounds.overlaps(tile.bounds)) return true;
                }
            }
        }
        return false;
    }
}
