import java.util.*;
import java.io.*;
import java.net.*;


public class BatThomi {

    int[][] grid;
    private int tries = 70;


    public BatThomi(int[] boats)
    {

        populate();
        for (int i = 0;i< boats.length;i++) {
            place_boat(boats[i]);
        }

    }

    private int check_win() {
        if(tries ==0)
            return -1;
        for(int i =0;i < 10;i++)
        {
            for(int j =0;j<10;j++)
            {
                if(grid[i][j] > 0)
                {
                    return 0;
                }
            }
        }
        return 1;
    }

    public int[] peekabou()
    {
        //init to 0 already
        int[] view = new int[100];

        for(int i =0;i<100;i++)
        {
            //touché
            if(grid[i%10][(i/10)%10] <=0)
                view[i] = -grid[i%10][(i/10)%10];
                //plouf
            else
                view[i] = 8;
        }

        return view;
    }

    public int boom(int id)
    {
        int posx = id%10;
        int posy = (id/10)%10;

        if(grid[posx][posy] == 8)
            grid[posx][posy] = 0;
        grid[posx][posy] = -grid[posx][posy];
        tries--;

        return -grid[posx][posy];
    }


    public void populate() {
        this.grid = new int [10][10];
            for(int i = 0; i < 10; i++)
                Arrays.fill(grid[i],8);
    }

    public void place_boat(int badassery)
    {
        //get the position
        Random rand = new Random();

        boolean not_good = true;

        int dir = rand.nextBoolean() ? 1 : 0;

        int posx = 0;
        int posy = 0;

        while(not_good)
        {
            not_good = false;
            posx = rand.nextInt(10 - badassery*Math.abs(dir));
            posy = rand.nextInt(10 - badassery*Math.abs(dir-1));

            //check if there isn't a boat in the position already
            for( int i = 0; i <badassery ;i++)
            {
                if(grid[posx + i *Math.abs(dir)][posy + i *Math.abs(dir-1)] != 8)
                {
                    not_good = true;
                }
            }
        }
        for( int i = 0; i <badassery ;i++)
        {
            grid[posx + i *Math.abs(dir)][posy + i *Math.abs(dir-1)]  = badassery;

        }
    }

}
