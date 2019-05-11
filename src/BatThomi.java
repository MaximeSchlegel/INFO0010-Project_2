import java.util.*;
import java.io.*;
import java.net.*;


public class BatThomi {

    int[][] grid;


    public BatThomi(int[] boats)
    {

        populate();
        for (int i = 0;i< boats.length;i++) {
            place_boat(boats[i]);
        }

    }

    private boolean check_win() {
        for(int i =0;i < 10;i++)
        {
            for(int j =0;j<10;j++)
            {
                if(grid[i][j] > 0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] peekabo()
    {
        //init to 0 already
        int[] view = new int[100];

        for(int i =0;i<100;i++)
        {
            //touché
            if(grid[i%10][(i/10)%10] >0)
                view[i] = 1;
                //plouf
            else if(grid[i%10][(i/10)%10] ==0)
                view[i] = 2;
        }

        return view;
    }

    public boolean boom(int id)
    {
        int posx = id%10;
        int posy = (id/10)%10;
        if(grid[posx][posy] == 8)
            grid[posx][posy] = 0;
        grid[posx][posy] = -grid[posx][posy];

        return (grid[posx][posy] !=0);
    }


    public void populate() {
        grid = new int [10][10];
        Arrays.fill(grid,8);
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

                if(grid[posx + i *Math.abs(dir)][posy + i *Math.abs(dir-1)] != 0)
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
