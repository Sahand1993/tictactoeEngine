package TTT3D;

public class Tester {
    public static void main(String[] args){
        long start = 0;
        long end = 0;
        long totalTime = 0;
        int times = 4;

        Player player = new Player();
        Player.depth = 5;
        Deadline deadline = new Deadline(Deadline.getCpuTime() + 1000000000);
        GameState myState;
        for(int i = 0; i < times; i++) {
            start = System.currentTimeMillis();
            String myStr = "ooo.....xx...................................................... 0_14_2 x"; // gameState.toMessage()-style string here
            myState = new GameState(myStr);
            System.err.println("start");
            System.err.println(myState.toString(1));
            GameState newState = player.play(myState, deadline);
            System.err.println("end");
            System.err.println(newState.toString(1));

            myStr = "ooo.o...xxx.x.x................................................. 0_14_2 x"; // gameState.toMessage()-style string here
            myState = new GameState(myStr);
            System.err.println("start");
            System.err.println(myState.toString(1));
            newState = player.play(myState, deadline);
            System.err.println("end");
            System.err.println(newState.toString(1));

            myStr = "................................................................ 0_14_2 x"; // gameState.toMessage()-style string here
            myState = new GameState(myStr);
            System.err.println("start");
            System.err.println(myState.toString(1));
            newState = player.play(myState, deadline);
            System.err.println("end");
            System.err.println(newState.toString(1));

            myStr = "o..xo........................................................... 0_14_2 x"; // gameState.toMessage()-style string here
            myState = new GameState(myStr);
            System.err.println("start");
            System.err.println(myState.toString(1));
            newState = player.play(myState, deadline);
            System.err.println("end");
            System.err.println(newState.toString(1));

            myStr = "ooo.o...xxx..................................................... 0_14_2 x"; // gameState.toMessage()-style string here
            myState = new GameState(myStr);
            System.err.println("start");
            System.err.println(myState.toString(1));
            newState = player.play(myState, deadline);
            System.err.println("end");
            System.err.println(newState.toString(1));

            end = System.currentTimeMillis();
            totalTime += end - start;
        }

        System.err.printf("depth: %d\ntime to execute: %d\n", player.depth, totalTime / times);


    }
}
