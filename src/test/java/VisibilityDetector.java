import java.util.ArrayList;
import java.util.Scanner;

public class VisibilityDetector {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int row, col;
        int my_base_x, my_base_y;
        int opp_base_x, opp_base_y;


        row = scanner.nextInt();
        col = scanner.nextInt();

        for(int i = 0 ; i < row ; i++) {
            String str = scanner.next();
        }

        my_base_x = scanner.nextInt();
        my_base_y = scanner.nextInt();


        opp_base_x = scanner.nextInt();
        opp_base_y = scanner.nextInt();

        for(int i = 0 ; i < 3 ; i++) {
            String powerName;
            int price, damage;
            powerName = scanner.next();
            price = scanner.nextInt();
            damage = scanner.nextInt();
        }

        System.err.print("Entering game loop\n");
        int captured = 0;
        while (true) {
            int my_score, opp_score;
            int my_flag_x, my_flag_y, my_carrier;
            int opp_flag_x, opp_flag_y, opp_carrier;
            int alive_cnt, opp_seen_cnt;
            int visible_coin_cnt;

            my_score = scanner.nextInt();
            opp_score = scanner.nextInt();


            my_flag_x = scanner.nextInt();
            my_flag_y= scanner.nextInt();
            my_carrier = scanner.nextInt();

            opp_flag_x = scanner.nextInt();
            opp_flag_y= scanner.nextInt();
            opp_carrier = scanner.nextInt();

            alive_cnt = scanner.nextInt();
            ArrayList<Integer>ids = new ArrayList<>(alive_cnt);
            ArrayList<Integer>xs = new ArrayList<>(alive_cnt);
            ArrayList<Integer>ys = new ArrayList<>(alive_cnt);
            ArrayList<Integer>cols = new ArrayList<>(alive_cnt);


            for(int i = 0 ; i < alive_cnt ; i++) {
                int id, x, y, health, timeout;
                id = scanner.nextInt();
                x = scanner.nextInt();
                y = scanner.nextInt();
                health = scanner.nextInt();
                timeout = scanner.nextInt();
                ids.add(id);
                xs.add(x);
                ys.add(y);
            }

            opp_seen_cnt = scanner.nextInt();

            for(int i = 0 ; i < opp_seen_cnt ; i++) {
                int id, x, y, health, timeout;
                id = scanner.nextInt();
                x = scanner.nextInt();
                y = scanner.nextInt();
                health = scanner.nextInt();
                timeout = scanner.nextInt();
                System.err.println("Seeing " + id + " " + x + " " + y + " " + health + " " + timeout);
            }

            visible_coin_cnt = scanner.nextInt();
            int coin_x=1,coin_y=1;
            for(int i = 0 ; i < visible_coin_cnt ; i++) {
                int x, y;
                x = scanner.nextInt();
                y = scanner.nextInt();

                coin_x = x;
                coin_y = y;
                for(int j=0;j<alive_cnt;j++)
                {
                    if(xs.get(j)==x && ys.get(j)==y)
                    {
                        cols.add(ids.get(j));
                    }
                }
            }


            StringBuilder str = new StringBuilder();
            for(int i = 0 ; i < alive_cnt ; i++) {

                if(i > 0) str.append(" | ");
//                int temp=0;
//
//                for(int j=0;j<cols.size();j++)
//                {
//                    if(cols.get(j)==ids.get(i))
//                    {
//                        str.append(String.format("COLLECT %d", ids.get(i)) );
//                        temp=1;
//                        break;
//                    }
//                }
//                if(temp==1)continue;
                if(visible_coin_cnt!=0)
                {
                    str.append(String.format("MOVE %d %d %d", ids.get(i), coin_x, coin_y) );
                    continue;
                }
                if(captured == 1) {
                    str.append(String.format("MOVE %d %d %d", ids.get(i), my_base_x, my_base_y) );
                }
                else {
                    if(opp_carrier != -1) captured = 1;
                    str.append(String.format("MOVE %d %d %d", ids.get(i), opp_base_x, opp_flag_y) );
                }
            }
            System.out.println(str);
        }
    }
}
