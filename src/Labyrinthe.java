class Labyrinthe extends Program {

    final int N = 1;
    final int S = 2;
    final int E = 4;
    final int W = 8;

    int width = 10;
    int height = 10;
    // void algorithm() {

    // }

    Carte creer_labyrinthe(int l_width, int l_height) {
        width = l_width;
        height = l_height;
        Carte carte = new Carte();
        carte.t_x = width * 2;
        carte.t_y = height * 2;

        int[][] grid = new int[height][width];
        carve_passages_from(0, 0, grid);
        LabyTuile[][] t = show(grid);

        Tuile[][] map = new Tuile[height * 2][width * 2];
        // for(int i = 0; i < length(width); i += 1) {
        //     Tuile tuile = new Tuile();
        //     tuile.id = 2;
        //     map[0][i] = tuile;
        // }
        // for(int i = 0; i < length(height); i += 1) {
        //     Tuile tuile = new Tuile();
        //     tuile.id = 2;
        //     map[i][0] = tuile;
        // }
        for(int y = 0; y < height; y += 1) {
            for(int x = 0; x < width; x += 1) {
                for(int iy = 0; iy < 2; iy += 1) {
                    for(int ix = 0; ix < 2; ix += 1) {
                        int gx = x * 2;
                        int gy = y * 2;
                        
                        Tuile tuile = new Tuile();
                        tuile.id = 1;
                        map[gy][gx] = tuile;
                        
                        tuile = new Tuile();
                        tuile.id = 2;
                        map[gy + 1][gx + 1] = tuile;

                        tuile = new Tuile();
                        if(t[y][x].south) {
                            tuile.id = 1;
                        } else {
                            tuile.id = 2;
                        }
                        map[gy + iy][gx] = tuile;


                        tuile = new Tuile();
                        if(t[y][x].east) {
                            tuile.id = 1;
                        } else {
                            tuile.id = 2;
                        }
                        map[gy][gx + ix] = tuile;
                    }
                }
            }
        }
        carte.grille = map;
        return carte;
    }

    LabyTuile[][] show(int[][] grid) {
        LabyTuile[][] tableau = new LabyTuile[height][width];
        for(int y = 0; y < height; y += 1) {
            for(int x = 0; x < width; x += 1) {
                LabyTuile t = new LabyTuile();
                
                t.north = (N & grid[y][x]) != 0;
                t.south = (S & grid[y][x]) != 0;
                t.east = (E & grid[y][x]) != 0;
                t.west = (W & grid[y][x]) != 0;
                if(x > 0 && x < width - 1 || y > 0 && y < height - 1) {
                    if(!t.north) {
                        t.north = random() > 0.8;
                    }
                    if(!t.south) {
                        t.south = random() > 0.8;
                    }
                    if(!t.east) {
                        t.east = random() > 0.8;
                    }
                    if(!t.west) {
                        t.west = random() > 0.8;
                    }
                    
                }
                tableau[y][x] = t;
            }
        }
        return tableau;
    }

    int[] random_directions() {
        int[] directions = new int[]{N, S, E, W};
        int[] r_dir = new int[4];
        for(int i = 0; i < 4; i += 1) {
            int entier = (int) (random() * (double) length(directions));
            r_dir[i] = directions[entier];

            int nbr = 0;
            int[] tampon = directions;
            directions = new int[4 - (i + 1)];
            for(int j = 0; j <= 4 - (i + 1); j += 1) {
                if(tampon[entier] != tampon[j]) {
                    directions[nbr] = tampon[j];
                    nbr += 1;
                }
            }

        }
        return r_dir;
    }

    void carve_passages_from(int cx, int cy, int[][] grid) {
        int[] directions = random_directions();
        
        for(int i = 0; i < length(directions); i += 1) {
            int direction = directions[i];
            int nx = cx + dx(direction);
            int ny = cy + dy(direction);
            if(between(ny, 0, height - 1) && between(nx, 0, width - 1) && grid[ny][nx] == 0) {
                grid[cy][cx] = grid[cy][cx] | direction;
                grid[ny][nx] = grid[ny][nx] | opposite(direction);
                carve_passages_from(nx, ny, grid);
            }
        }
    }

    boolean between(int val, int min, int max) {
        return val >= min && val <= max;
    }

    int dx(int direction) {
        int valeur = 0;
        if(direction == E) {
            valeur = 1;
        } else if(direction == W) {
            valeur = -1;
        }
        return valeur;
    }
    
    int dy(int direction) {
        int valeur = 0;
        if(direction == S) {
            valeur = 1;
        } else if(direction == N) {
            valeur = -1;
        }
        return valeur;
    }

    int opposite(int direction) {
        int valeur = 0;
        if(direction == S) {
            valeur = N;
        } else if(direction == N) {
            valeur = S;
        } else if(direction == E) {
            valeur = W;
        } else if(direction == W) {
            valeur = E;
        }
        return valeur;
    }
}