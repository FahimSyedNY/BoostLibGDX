package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Level {
    Texture tileset;
    TextureRegion[][] tiles;
    int[][] map = new int[][] {
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
        {13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24},
        {25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36},
        {37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48},
        {49, 50 , 51, 52, 53, 54, 55, 56, 57, 58, 59, 60},
        {61, 62, 63, 64, 65, 66, 67,  68, 69, 70, 71, 72},
    };

    public Level() {
        tileset = new Texture(Gdx.files.internal("maptiles/Tileset.png"));
        tiles = TextureRegion.split(tileset, 32, 32);
    }

    public void render(SpriteBatch batch) {
        int tilesetWidthInTiles = tiles[0].length;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                int tileID = map[i][j] - 1;
                if (tileID >= 0) {
                    int tileRow = tileID / tilesetWidthInTiles;
                    int tileCol = tileID % tilesetWidthInTiles;
                    batch.draw(tiles[tileRow][tileCol], j * 32, i * 32);
                }
            }
        }
    }
}
