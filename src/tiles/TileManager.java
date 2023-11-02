package tiles;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {
    GamePanel gamePanel;
    Tile[] tile;
    final int numTiles = 1;

    int mapTileNum[][];

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tile = new Tile[numTiles];
        mapTileNum = new int[gamePanel.getMaxScreenRows()][gamePanel.getMaxScreenCols()];
        getTileImage();
        loadMap("/maps/map01.txt");
    }

    public void getTileImage() {
        try {
            for (int i = 0; i < tile.length; i++) {
                tile[i] = new Tile();
                tile[i].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tile" + (i + 1) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            BufferedReader  br = new BufferedReader(new InputStreamReader(in));

            int col = 0;
            int row = 0;

            while(col < gamePanel.getMaxScreenCols() && row < gamePanel.getMaxScreenRows()){
                String line = br.readLine();

                while(col < gamePanel.getMaxScreenCols()){
                    String[] numbers = line.split("\s+");
                    mapTileNum[row][col] = Integer.parseInt(numbers[col]);
                    col++;
                }

                if(col == gamePanel.getMaxScreenCols()){
                    col = 0;
                    row++;
                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d){
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;
        int tileNum = 0;

        while(col < gamePanel.getMaxScreenCols() && row < gamePanel.getMaxScreenRows()){
            x = col * gamePanel.getTileSize();
            y = row * gamePanel.getTileSize();
            tileNum = mapTileNum[row][col];

            g2d.drawImage(tile[tileNum].image, x, y, gamePanel.getTileSize(), gamePanel.getTileSize(), null);

            col++;
            if(col == gamePanel.getMaxScreenCols()){
                col = 0;
                row++;
            }
        }
    }
}
