class ListeAleatoire extends Program {



    void algorithm() {
        int[] directions = new int[]{1, 2, 3, 4};
        int[] r_dir = new int[4];
        for(int i = 0; i < 4; i += 1) {
            int entier = (int) (random() * (double) length(directions));
            r_dir[i] = directions[entier];
            for(int g = 0; g < length(directions); g += 1) {
                if(g == entier) {
                    print("!");
                }
                print(directions[g] + ", ");
            }
            println(" ");

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
        for(int i = 0; i < 4; i += 1) {
            print(r_dir[i] + ", ");
        }
    }
}