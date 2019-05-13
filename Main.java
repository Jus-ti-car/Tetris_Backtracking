import java.util.Scanner;

class Object {
    int x_left, x_right, y_left, y_right;
    int square;
    int type;
    int id;
    boolean is_used;

    public Object(int x_left, int x_right, int y_left, int y_right, int type, int id) {
        this.x_left = x_left;
        this.x_right = x_right;
        this.y_left = y_left;
        this.y_right = y_right;
        this.square = (x_right - x_left) * (y_right - y_left);
        this.type = type;
        this.is_used = false;
        this.id = id; //appointing id for object
    }

    public Object(int square, int type, int id) {
        this.x_left = -1000000;
        this.x_right = -1000000;
        this.y_left = -1000000;
        this.y_right = -1000000;
        this.square = square;
        this.type = type;
        this.is_used = false;
        this.id = id;
    }

    public static boolean intersect(Object a, Object b) { //checking whether objects coincide
        boolean check_x = false;
        boolean check_y = false;
        if (a.x_left <= b.x_left && a.x_right >= b.x_left) check_x = true;
        if (b.x_left <= a.x_left && b.x_right >= a.x_left) check_x = true;
        if (a.y_left <= b.y_left && a.y_right >= b.y_left) check_y = true;
        if (b.y_left <= a.y_left && b.y_right >= a.y_left) check_y = true;
        return check_x && check_y;
    }
}

public class Main {

    static Object[] objects;
    static int[][] used; //need to color used places
    static boolean[][] adj_matrix;
    static int N_size, M_size; //matrix sizes

    static boolean is_ok(Object a, Object b) { //checking whether objects can stay together
        int min_id = Math.min(a.id, b.id) - 1;
        int max_id = Math.max(a.id, b.id) - 1;
        if (adj_matrix[min_id][max_id] && Object.intersect(a, b)) {
            return false;
        }
        if (adj_matrix[max_id][min_id] && !Object.intersect(a, b)) {
            return false;
        }
        return true;
    }

    static void recursion(int remaining, int N, int lastX, int lastY) { //the core function
        if (remaining == 0) { //if all objects are placed
            System.out.println("Solution found:");
            for (int i = 0; i < N_size; i++) {
                for (int j = 0; j < M_size; j++) {
                    System.out.print(used[i][j] + " ");
                }
                System.out.println();
            }
            System.exit(0);
            return;
        }
        if (lastX == N_size - 1 && lastY == M_size - 1) return; //end of the matrix
        for (int X = lastX; X < N_size; X++) {
            for (int Y = 0; Y < M_size; Y++) {
                if (X == lastX && Y <= lastY) continue;
                if (used[X][Y] != 0) continue;
                for (int i = 0; i <= N; i++)
                {
                    if (i == N) { //i == N is for just leaving this cell free
                        recursion(remaining, N, X, Y);
                        continue;
                    }
                    if (objects[i].is_used) continue;
                    int x_sz = -1, y_sz = -1;
                    if (objects[i].type == 2) { //if object's shape is determined
                        x_sz = objects[i].x_right - objects[i].x_left;
                        y_sz = objects[i].y_right - objects[i].y_left;
                    }
                    else { // otherwise

                        for (int mod = 1; mod * mod <= objects[i].square; mod++) { //choosing object's shape
                            if (objects[i].square % mod != 0) continue; //if other size is non-integer
                            x_sz = mod;
                            y_sz = objects[i].square / mod;
                            if (X + x_sz > N_size || Y + y_sz > M_size) continue; //if it can't fit matrix
                            boolean flag = false;
                            for (int x = X; x - X < x_sz; x++) {
                                if (flag) break;
                                for (int y = Y; y - Y < y_sz; y++) {
                                    if (flag) break;
                                    if (used[x][y] != 0) flag = true; //if one of cells is already taken
                                }
                            }
                            objects[i].x_left = X;
                            objects[i].x_right = X + x_sz;
                            objects[i].y_left = Y;
                            objects[i].y_right = Y + y_sz;
                            for (int cur = 0; cur < N; cur++) { //checking with adjacency matrix
                                if (!objects[cur].is_used || flag) continue;
                                if (!is_ok(objects[i], objects[cur])) {
                                    flag = true;
                                }
                            }
                            if (!flag) { // if everything is ok, placing our object
                                for (int x = X; x - X < x_sz; x++) {
                                    for (int y = Y; y - Y < y_sz; y++) {
                                        used[x][y] = objects[i].id;
                                    }
                                }
                                objects[i].is_used = true;
                                recursion(remaining - 1, N, X, Y); //starting next step
                                for (int x = X; x - X < x_sz; x++) { //returning everything back
                                    for (int y = Y; y - Y < y_sz; y++) {
                                        used[x][y] = 0;
                                    }
                                }
                                objects[i].is_used = false;
                            }
                        }

                        for (int mod = 1; mod * mod <= objects[i].square; mod++) { //now row size is smaller one
                            if (objects[i].square % mod != 0) continue;
                            y_sz = mod;
                            x_sz = objects[i].square / mod;
                            if (X + x_sz > N_size || Y + y_sz > M_size) continue;
                            boolean flag = false;
                            for (int x = X; x - X < x_sz; x++) {
                                if (flag) break;
                                for (int y = Y; y - Y < y_sz; y++) {
                                    if (flag) break;
                                    if (used[x][y] != 0) flag = true;
                                }
                            }
                            objects[i].x_left = X;
                            objects[i].x_right = X + x_sz;
                            objects[i].y_left = Y;
                            objects[i].y_right = Y + y_sz;
                            for (int cur = 0; cur < N; cur++) {
                                if (!objects[cur].is_used || flag) continue;
                                if (!is_ok(objects[i], objects[cur])) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                for (int x = X; x - X < x_sz; x++) {
                                    for (int y = Y; y - Y < y_sz; y++) {
                                        used[x][y] = objects[i].id;
                                    }
                                }
                                objects[i].is_used = true;
                                recursion(remaining - 1, N, X, Y);
                                for (int x = X; x - X < x_sz; x++) {
                                    for (int y = Y; y - Y < y_sz; y++) {
                                        used[x][y] = 0;
                                    }
                                }
                                objects[i].is_used = false;
                            }
                        }
                        continue;

                    }
                    if (X + x_sz > N_size || Y + y_sz > M_size) continue;
                    boolean flag = false;
                    for (int x = X; x - X < x_sz; x++) {
                        if (flag) break;
                        for (int y = Y; y - Y < y_sz; y++) {
                            if (flag) break;
                            if (used[x][y] != 0) flag = true;
                        }
                    }
                    objects[i].x_left = X;
                    objects[i].x_right = X + x_sz;
                    objects[i].y_left = Y;
                    objects[i].y_right = Y + y_sz;
                    for (int cur = 0; cur < N; cur++) {
                        if (!objects[cur].is_used || flag) continue;
                        if (!is_ok(objects[i], objects[cur])) {
                            System.out.println(i + " " + cur + " " + X + " " + Y);
                            flag = true;
                        }
                    }
                    if (flag) continue;
                    for (int x = X; x - X < x_sz; x++) {
                        for (int y = Y; y - Y < y_sz; y++) {
                            used[x][y] = objects[i].id;
                        }
                    }
                    objects[i].is_used = true;
                    recursion(remaining - 1, N, X, Y);
                    for (int x = X; x - X < x_sz; x++) {
                        for (int y = Y; y - Y < y_sz; y++) {
                            used[x][y] = 0;
                        }
                    }
                    objects[i].is_used = false;

                }
            }
        }


    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        N_size = in.nextInt(); //Scanning shape of main matrix
        M_size = in.nextInt();

        int N = in.nextInt(); //Scanning number of objects

        objects = new Object[N];
        for (int i = 0; i < N; i++) {
            int type = in.nextInt(); //Scanning type of object:1 - typeA, 2 - typeB, 3 - typeC
            if (type != 1) { //if shape is determined - scanning it
                int x_left = in.nextInt();
                int y_left = in.nextInt();
                int x_right = in.nextInt();
                int y_right = in.nextInt();
                objects[i] = new Object(x_left, x_right, y_left, y_right, type, i + 1);
            } else { //else scanning square of object
                int square = in.nextInt();
                objects[i] = new Object(square, type, i + 1);
            }
        }
        adj_matrix = new boolean[N][N]; //Adjacency matrix
        used = new int[N_size][M_size]; //Our square
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                adj_matrix[i][j] = in.nextBoolean();
            }
        }
        int counter = 0;
        for (int i = 0; i < N; i++) //Placing type-C objects
        {
            if (objects[i].type == 3) {
                counter++;
                for (int X = objects[i].x_left; X < objects[i].x_right; X++) {
                    for (int Y = objects[i].y_left; Y < objects[i].y_right; Y++) {
                        if (used[X][Y] != 0) {
                            System.out.println("Impossible situation");
                            return;
                        }
                        used[X][Y] = objects[i].id;
                    }
                }
                objects[i].is_used = true;
            }
        }
        recursion(N - counter, N, 0, -1);
        System.out.println("Impossible situation");
    }

}