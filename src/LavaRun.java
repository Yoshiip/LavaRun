import extensions.CSVFile;
import extensions.File;

class LavaRun extends Program {

    boolean perdu = false;
    int mode_de_jeu = 1;

    int tour = 0;
    int total_tuiles_explorer = 0;
    int longueur = 10;
    int hauteur = 10;

    boolean fini = false;
    boolean defaite = false;

    final String TEXTE_LAVARUN = "../resources/texte_lavarun.txt";
    final String TEXTE_AIDE = "../resources/texte_aide.txt";
    final String TEXTE_100 = "../resources/texte_100.txt";
    final String TEXTE_RECORDS = "../resources/texte_records.txt";

    final String CSV_RECORDS = "../resources/records.csv";
    final String QUESTIONS_CSV = "../resources/questions.csv";

    /*  1. Normal
        2. Difficile
    */


    // Pour mettre de la couleur dans le terminal.
    // Regular Colors

    public static final String RESET = "\033[0m";  // Text Reset

    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    void afficher_cadre(String[] question) {
        int longueur = length(question[0]) + 4;
        int hauteur = 5;
        for(int x = 0; x < hauteur; x += 1) {
            for(int y = 0; y < longueur; y += 1) {
                if(x == 0 || x == hauteur - 1) {
                    if(y == 0 || y == longueur - 1) {
                        print("+");
                    } else {
                        print("-");
                    }
                } else if(y == 0 || y == longueur - 1) {
                    print("|");
                } else {
                    if(x == hauteur / 2) {
                        if(y > 1 && y < length(question[0])) {
                            print(charAt(question[0], y - 2));
                        } else {
                            print(" ");
                        }
                    } else {
                        print(" ");
                    }
                }
            }
            println();
        }
    }

    void algorithm() {        
        afficher_menu();
    }


    // Cette fonction permet d'afficher un texte contenu dans un fichier texte.
    void afficher_grand_texte(String chemin_fichier, String prefixe) {
        File fichier = newFile(chemin_fichier);

        print(prefixe);
        while(ready(fichier)){
            println(readLine(fichier));
        }
        print(RESET);
        
    }

    void afficher_menu() {                 
        boolean choisi = false;
        while(!choisi) {
            afficher_grand_texte(TEXTE_LAVARUN, RED_BOLD);
            println(WHITE_BOLD + "\n   Par Aymeri Tourneur et Antoine Banse\n" + RESET);
            println(BLACK + "Selectionnez une option :");
            println(GREEN_BOLD + "1. Jouer");
            println(BLUE_BOLD + "2. Questions" + RESET);
            println(YELLOW_BOLD + "3. Records");
            println(PURPLE_BOLD + "4. Comment jouer");
            println(RED_BOLD + "5. Quitter" + RESET);
            String choix = readString();
            if(equals(choix, "1")) {
                jouer_menu();
            } else if(equals(choix, "2")) {
                menu_questions();
            } else if(equals(choix, "3")) {
                menu_records();
            } else if(equals(choix, "4")) {
                menu_aide();
            } else if(equals(choix, "5")) {
                println(BLUE_BOLD + "Merci d'avoir joué!" + RESET);
                choisi = true;
            }
        }
    }
    

    void jouer_menu() {
        boolean choisi = false;
        while(!choisi) {
            println(WHITE_BOLD + "\n\nSelectionnez une difficulté:");
            println(GREEN_BOLD + "1 - Normal" + RESET);
            println("L'expérience de base de LabyRun.");
            println(RED_BOLD + "2 - Difficile" + RESET);
            println("Un labyrinthe plus grand, avec plus d'intersections, une lave plus rapide et des questions plus difficiles!");
            println(PURPLE_BOLD + "3 - Personalisée" + RESET);
            println("Un labyrinthe avec vos propres règles!");
            println("Ecrivez " + CYAN_BOLD + "[retour]" + WHITE + " pour retourner en arrière.");
            String entree = readString();
            if(equals(entree, "retour")) {
                return;
            } else if(equals(entree, "1") || equals(entree, "2") || equals(entree, "3")) {
                mode_de_jeu = stringToInt(entree);
                commencer_partie();
                choisi = true;
            }
        }
    }



    void commencer_partie() {
        longueur = 10;
        hauteur = 10;
        vitesse_lave = 2;
        if(mode_de_jeu == 2) {
            vitesse_lave = 1;
            longueur = 20;
            hauteur = 12;
        }
        if(mode_de_jeu == 3) {
            println("Entrez la taille horizontale du labyrinthe: ");
            longueur = readInt() / 2;
            println("Entrez la taille verticale du labyrinthe: ");
            hauteur = readInt() / 2;
            println("Vitesse de la lave: ");
            vitesse_lave = readInt();
        }
        Partie partie = creerPartie(longueur, hauteur);
        fini = false;
        defaite = false;
        partie.carte = explorerCasesAdjacentes(partie.carte, partie.joueur.x, partie.joueur.y);
        long temps = getTime();
        tour = 0;
        total_tuiles_explorer = 0;
        while(!fini) {
            println(toString(partie));
            println("                      " + PURPLE_BOLD +"    z" + RESET);
            println(WHITE_BOLD + "Entrez une direction :" + PURPLE_BOLD +"  q 🟆 d" + RESET);
            println(BLACK + " (" + CYAN_BOLD + "[fin]" + BLACK + "  pour quitter) " + PURPLE_BOLD +"    s" + RESET);
            String saisie = readString();
            if(equals(saisie, "fin")) {
                fini = true;
                defaite = true;
            }
            if(length(saisie) > 0) {
                if(charAt(saisie, 0) == 'z') {
                    deplacement(partie, 0, -1);
                } else if(charAt(saisie, 0) == 'q') {
                    deplacement(partie, -1, 0);
                } else if(charAt(saisie, 0) == 's') {
                    deplacement(partie, 0, 1);
                } else if(charAt(saisie, 0) == 'd') {
                    deplacement(partie, 1, 0);
                }
                if(partie.joueur.x == partie.carte.t_x - 1 && partie.joueur.y == partie.carte.t_y - 2) {
                    fini = true;
                }
                if(partie.carte.grille[partie.joueur.y][partie.joueur.x].id == 3) {
                    fini = true;
                    defaite = true;
                }
            }
        }
        // Quand la partie est terminé
        if(defaite) {
            println(GREEN_BOLD + "Perdu :'(" + RESET);
            println(BLACK + "La lave vous a brûlé." + RESET);

        } else {
            println(GREEN_BOLD + "Félicitations!" + RESET);
            println(BLACK + "Vous avez réussi à vous échapper du labyrinthe." + RESET);
        }
        int pourcentage = (int)((((double) total_tuiles_explorer) / (longueur * hauteur * 4.0)) * 100.0); // pourcentage de tuile exploré
        println("Pourcentage du labyrinthe exploré : " + GREEN_BOLD + pourcentage + "%." + RESET);
        if(pourcentage == 100) {
            afficher_grand_texte(TEXTE_100, YELLOW_BOLD);
            println("Félicitations! Vous avez exploré intégralement le labyrinthe!");
            println(BLACK + "Bravo pour votre exploit! " + RESET);
        }
        println("Temps total : " + GREEN_BOLD + (int) ((getTime() - temps) / 1000.0) + "s" + RESET);
        println("Souhaitez vous sauvegarder votre temps et votre exploration ? (oui/non)");
        String entree = readString();
        if(equals(entree, "oui")) {
            CSVFile records = loadCSV(CSV_RECORDS);
            String[][] nouveaux_records = new String[rowCount(records) + 1][columnCount(records)];
            for(int y = 0; y < rowCount(records); y++) {
                for(int x = 0; x < columnCount(records); x++) {
                    nouveaux_records[y][x] = getCell(records, y, x);
                }
            }

            nouveaux_records[rowCount(records)][0] = "" + (int) ((getTime() - temps) / 1000.0);
            println("Quelle est votre nom ?");
            nouveaux_records[rowCount(records)][1] = readString();
            nouveaux_records[rowCount(records)][2] = "" + total_tuiles_explorer;
            nouveaux_records[rowCount(records)][3] = "" + mode_de_jeu;

            saveCSV(nouveaux_records, CSV_RECORDS);
            println("Votre temps à été sauvegardé!");
        }
        
    }

    // Cette fonction remplit le labyrinthe de lave
    Carte coulerLave(Carte carte) {
        Vecteur[] v = new Vecteur[carte.t_x * carte.t_y]; // On créer une liste de vecteurs.
        int v_i = 0;
        for(int y = 0; y < carte.t_y; y += 1) {
            for(int x = 0; x < carte.t_x; x += 1) {
                if(carte.grille[y][x].id == 3) { // Si la case est de la lave, on l'ajoute dans une liste
                    Vecteur vecteur = new Vecteur();
                    vecteur.x = x;
                    vecteur.y = y;
                    v[v_i] = vecteur;
                    v_i += 1;

                }
            }
        }

        for(int i = 0; i < length(v); i += 1) { // Et ensuite pour chaque élément de la liste, on fait couler la lave dans les 4 cases adjacentes.
            if(v[i] != null) {
                for(int y = -1; y < 2; y += 1) {
                    for(int x = -1; x < 2; x += 1) {
                        if(dansZoneDeJeu(carte, v[i].x + x, v[i].y + y) && abs(x) != abs(y)) {
                            carte = ajouterLave(carte, v[i].x + x, v[i].y + y);
                        }
                    }
                }
            }
        }

        return carte;
    }

    void testCoulerLave() {
        Carte test_carte = creerCarte(5, 5);
        test_carte.grille[0][0].id = 3; // lave

        // on remplit le labyrinthe de lave
        for(int i = 0; i < 5*5*5; i += 1) {
            coulerLave(test_carte);
        }
        for(int y = 0; y < 5; y += 1) {
            for(int x = 0; x < 5; x += 1) {
                assertNotEquals(1, test_carte.grille[y][x].id);
            }
        }
    }

    Carte ajouterLave(Carte carte, int x, int y) {
        if(dansZoneDeJeu(carte, x, y)) {
            if(carte.grille[y][x].id == 1) {
                Tuile tuile = new Tuile();
                tuile.explorer = carte.grille[y][x].explorer;
                tuile.id = 3;
                carte.grille[y][x] = tuile;
            }
        }
        return carte;
    }

    int vitesse_lave = 2;
    int chrono_lave = 2;

    Partie deplacement(Partie p, int x, int y) {
        if(dansZoneDeJeu(p.carte, p.joueur.x + x, p.joueur.y + y)) {
            if(p.carte.grille[p.joueur.y + y][p.joueur.x + x].id != 2) {
                tour += 1;
                if(tour == 5) {
                    Tuile tuile = new Tuile();
                    tuile.id = 3;
                    tuile.explorer = true;
                    p.carte.grille[0][0] = tuile;
                }
                chrono_lave -= 1;
                if(chrono_lave == 0) {
                    p.carte = coulerLave(p.carte);
                    chrono_lave = vitesse_lave;
                }

                p.joueur.x += x;
                p.joueur.y += y;
                clearScreen();
                println(toString(p));
                println();
                p.carte = explorerCasesAdjacentes(p.carte, p.joueur.x, p.joueur.y);
                delay(50);

                if(p.carte.grille[p.joueur.y][p.joueur.x].id == 3) { // si le joueur est sur de la lave
                    fini = true;
                    defaite = true;
                    return p; // pour arrêter le déplacement
                } else if(!contientIntersection(p, p.joueur.x, p.joueur.y)) {
                    deplacement(p, x, y);
                } else {
                    if(afficher_qcm()) {
                        return p;
                    } else {
                        deplacement(p, -x, -y);
                    }
                }
            }

        }
        return p;
    }

    boolean contientIntersection(Partie p, int px, int py) {
        int nb_choix = 0;
        for(int y = -1; y < 2; y += 1) {
            for(int x = -1; x < 2; x += 1) {
                if(abs(x) != abs(y) && dansZoneDeJeu(p.carte, px + x, py + y)) { // dans le carré 3x3, on ne s'intéresse pas aux diagonales
                    if(p.carte.grille[py + y][px + x].id == 1 || p.carte.grille[py + y][px + x].id == 4) { // on vérifie les 4 cases adjacentes
                        nb_choix++;
                    }
                }
            }
        }
        return nb_choix >= 3;
    }


    // Par défaut quand le joueur se promène, il ne voit pas les cases autour de lui. A chaque déplacement on explore les cases adjacentes dans un rayon de 1.
    Carte explorerCasesAdjacentes(Carte c, int x, int y) {
        for(int iy = -1; iy < 2; iy++) {
            for(int ix = -1; ix < 2; ix++) {
                if(dansZoneDeJeu(c, x + ix, y + iy)) {
                    if(!c.grille[y + iy][x + ix].explorer) {
                        total_tuiles_explorer += 1;
                    }
                    c.grille[y + iy][x + ix].explorer = true;
                }
            }
        }
        
        return c;
    }

    void testExplorerCasesAdjacentes() {
        Carte test_carte = creerCarte(5, 5);
        test_carte = explorerCasesAdjacentes(test_carte, 2, 2);
        assertEquals(true, test_carte.grille[2][2].explorer);
        assertEquals(true, test_carte.grille[2][3].explorer);
        assertEquals(false, test_carte.grille[0][1].explorer);
        assertEquals(false, test_carte.grille[3][4].explorer);
    }

    // Cette méthode permet de tester si une case est dans la zone de jeu.
    boolean dansZoneDeJeu(Carte c, int x, int y) {
        return x >= 0 && y >= 0 && x < c.t_x && y < c.t_y;
    }

    void testDansZoneDeJeu() {
        Partie test_partie = creerPartie(5, 5);
        assertEquals(true, dansZoneDeJeu(test_partie.carte, 0, 0));
        assertEquals(true, dansZoneDeJeu(test_partie.carte, 4, 2));
        assertEquals(false, dansZoneDeJeu(test_partie.carte, -4, 2));
        assertEquals(false, dansZoneDeJeu(test_partie.carte, 95, 47));
    }

    Partie creerPartie(int taille_x, int taille_y) {
        Partie partie = new Partie();
        partie.carte = creerCarte(taille_x, taille_y);
        partie.joueur = creerJoueur();
        return partie;
    }


    Joueur creerJoueur() {
        Joueur joueur = new Joueur();
        joueur.x = 0;
        joueur.y = 0;
        return joueur;
    }

    Carte creerCarte(int taille_x, int taille_y) {
        Labyrinthe labyrinthe = new Labyrinthe();
        Carte carte = labyrinthe.creer_labyrinthe(taille_x, taille_y);
        Tuile drapeau = new Tuile();
        drapeau.id = 4;
        carte.grille[taille_y * 2 - 2][taille_x * 2 - 1] = drapeau;
        return carte;
    }


    void testCreerCarte() {
        Carte test_carte = creerCarte(5, 5);
        assertEquals(4, test_carte.grille[8][9].id); // doit être le drapeau
        assertEquals(2, test_carte.grille[1][1].id); // doit être un mur
        assertEquals(1, test_carte.grille[0][0].id); // doit être un sol
    }

    String toString(Partie p) {
        String m = "";
        for(int y = 0; y < p.carte.t_y; y += 1) {
            for(int x = 0; x < p.carte.t_x; x += 1) {
                if(p.joueur.y == y && p.joueur.x == x) {
                    m += GREEN_BOLD + '☻';
                } else {
                    if(p.carte.grille[y][x].id == 4) {
                        m += GREEN + '⚑';
                    } else if(p.carte.grille[y][x].explorer) {
                        if(p.carte.grille[y][x].id == 1) {
                            m += WHITE + ' ';
                        } else if(p.carte.grille[y][x].id == 2) {
                            m += WHITE + '◼';
                        } else if(p.carte.grille[y][x].id == 3) {
                            m += RED_BOLD + '◼';
                        }
                    } else {
                        m += ANSI_BLACK + '?';
                    }
                }
                m += ' ';
            }
            m += "\n" + RESET;
        }
        return m;
    }

    boolean afficher_question(String[] question) {
        afficher_cadre(question);
        String entree = readString();
        if(equals(entree, question[1])) {
            println("Bravo");
            return true;
        } else {
            println("Perdu, la bonne réponse était " + question[1]);
            return false;
        }
    }

    // Quand le joueur se trouve dans une intersection, on va lui poser une question
    boolean afficher_qcm() {
        CSVFile qcm = loadCSV(QUESTIONS_CSV);
        
        // On prend une question au hasard
        int numero_question = (int) (random() * (double) rowCount(qcm));
        String reponse = getCell(qcm, numero_question, 0);

        // On affiche l'intitulé de la question
        println(WHITE_BOLD + getCell(qcm, numero_question, 1) + RESET);
        for(int i = 0; i < 4; i++) {
            println(i + 1 + ". " + getCell(qcm, numero_question, 2 + i));
        }

        String entree = readString();
        boolean bonne_reponse = equals(entree, reponse);

        for(int i = 1; i < 5; i++) {
            if(equals("" + i, reponse)) {
                println(GREEN_BOLD + i + ". " + getCell(qcm, numero_question, 2 + i - 1));
            } else if(equals("" + i, entree)) {
                println(WHITE_BOLD + i + ". " + getCell(qcm, numero_question, 2 + i - 1));
            } else {
                println(RESET + i + ". " + getCell(qcm, numero_question, 2 + i - 1));
            }
        }

        if(bonne_reponse) {
            println(GREEN_BOLD + "Bonne réponse!" + RESET);
        } else {
            println(RED_BOLD + "Mauvaise réponse..." + RESET);
            println("A cause de ça, vous êtes désorienté!");
        }
        println(WHITE_BOLD + "--- Appuyez sur entrée pour continuer ---" + RESET);
        readString();

        return bonne_reponse;
    }

    //     ____                  _   _                 
    //    / __ \                | | (_)                
    //   | |  | |_   _  ___  ___| |_ _  ___  _ __  ___ 
    //   | |  | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
    //   | |__| | |_| |  __/\__ \ |_| | (_) | | | \__ \
    //    \___\_\\__,_|\___||___/\__|_|\___/|_| |_|___/


    void menu_questions() {
        println("Bienvenue dans le menu des questions. Ici, vous pouvez: ");
        println("- Visualiser les différentes questions");
        println("- Faire les questions une par une.");
        println("- Créer de nouvelles questions");

        visualiser_questions();
    }

    void visualiser_questions() {
        boolean terminer = false;
        int id_question = 0;
        while(!terminer) {
            CSVFile qcm = loadCSV(QUESTIONS_CSV);
            int nombres_questions = rowCount(qcm);
            println(WHITE_BOLD + "----- AFFICHAGE DE LA QUESTION " + (id_question + 1) + "/" + nombres_questions + " -----" + RESET);
            println(getCell(qcm, id_question, 1));
            String reponse = getCell(qcm, id_question, 0);
            for(int i = 0; i < 4; i++) {
                if(equals(reponse, "" + (i + 1))) {
                    print(GREEN_BOLD);
                }
                println(i + 1 + ". " + getCell(qcm, id_question, 2 + i) + RESET);
            }
            println(WHITE_BOLD + "Naviguez entre les questions avec " + CYAN_BOLD + "[q]" + WHITE_BOLD + " et " + CYAN_BOLD + "[d]" + WHITE_BOLD + "." + RESET);
            println("\nEcrivez " + CYAN_BOLD + "[nouvelle]" + RESET + " pour créer une nouvelle question..");
            println("Ecrivez " + CYAN_BOLD + "[modifier]" + RESET + " pour modifier la question.");
            println(RED + "Ecrivez " + CYAN_BOLD + "[supprimer]" + RED + " pour supprimer la question." + RESET);
            println("Ecrivez " + CYAN_BOLD + "[quitter]" + WHITE_BOLD + " pour quitter." + RESET);

            String entree = readString();
            if(equals(entree, "q")) {
                id_question = id_question - 1;
                if(id_question == -1) {
                    id_question = nombres_questions - 1;
                }
            } else if(equals(entree, "d")) {
                id_question = (id_question + 1) % nombres_questions;
            } else if(equals(entree, "modifier")) {
                modifier_question(qcm, id_question);
            } else if(equals(entree, "supprimer")) {
                supprimer_question(qcm, id_question);
                id_question = 0;
            } else if(equals(entree, "nouvelle")) {
               creation_question(qcm);
            } else if(equals(entree, "quitter")) {
                terminer = true;
            }
        }
    }

    void creation_question(CSVFile qcm) {
        Question question = new Question();
        question.propositions = new String[]{"", "", "", ""};
        String saisie = prendre_saisie("Entrez la question: ");
        if(equals(saisie, "")) {
            return;
        }
        question.question = saisie;
        for(int i = 0; i < 4; i++) {
            saisie = prendre_saisie("Entrez la proposition n°" + (i + 1) + " :");
            if(equals(saisie, "")) {
                return;
            }
            question.propositions[i] = saisie;
        }
        saisie = prendre_saisie("Quelle est le numéro de proposition de la bonne réponse ?");
        if(equals(saisie, "")) {
            return;
        }
        question.reponse = saisie;
        sauvegarder_qcm(qcm, rowCount(qcm), question);
    }

    void modifier_question(CSVFile qcm, int id_question) {
        boolean terminer = false;
        Question question = new Question();
        question.propositions = new String[]{"", "", "", ""};
        if(id_question < rowCount(qcm)) {
            question.reponse = getCell(qcm, id_question, 0);
            question.question = getCell(qcm, id_question, 1);
            question.propositions[0] = getCell(qcm, id_question, 2);
            question.propositions[1] = getCell(qcm, id_question, 3);
            question.propositions[2] = getCell(qcm, id_question, 4);
            question.propositions[3] = getCell(qcm, id_question, 5);
        }

        while(!terminer) {
            println(WHITE_BOLD + "----- EDITEUR DE QUESTION -----" + RESET);
            println(CYAN_BOLD + "[q] " + RESET + question.question);
            for(int i = 0; i < 4; i++) {
                if(question.propositions[i] == "") {
                    println(CYAN_BOLD + "[" + (i + 1) +"] " + BLACK + "pas de proposition" + RESET);
                } else {
                    println(CYAN_BOLD + "[" + (i + 1) +"] " + RESET + question.propositions[i]);
                }
            }
            println(CYAN_BOLD + "[r] " + GREEN_BOLD + question.reponse);

            println(RED + "\nTapez " + CYAN_BOLD + "[fin]" + RED + " pour terminer de modifier la question." + RESET);
            String entree = readString();
            if(!equals(entree, "fin")) {
                terminer = true;
            } 


            if(equals(entree, "q")) {
                String saisie_question = prendre_saisie("\n Entrez l'énoncé de la question " + WHITE_BOLD + "> " + RESET);
                if(!equals(saisie_question, "")) {
                    question.question = saisie_question;
                }
            }



            if(equals(entree, "1") || equals(entree, "2") || equals(entree, "3") || equals(entree, "4")) {
                String saisie_question = prendre_saisie("\nEntrez la proposition n°" + stringToInt(entree) + " " + WHITE_BOLD + "> " + RESET);
                if(!equals(saisie_question, "")) {
                    question.propositions[stringToInt(entree) - 1] = saisie_question;
                }
            }
            if(equals(entree, "r")) {
                String saisie_question = prendre_saisie("\nEntrez le numéro de la proposition contenant la bonne réponse (1-4) " + WHITE_BOLD + "> " + RESET);
                while(!(equals(saisie_question, "1") || equals(saisie_question, "2") || equals(saisie_question, "3") || equals(saisie_question, "4")) || equals(saisie_question, "")) {
                    saisie_question = prendre_saisie("\nEntrez le numéro de la proposition contenant la bonne réponse (1-4) " + WHITE_BOLD + "> " + RESET);
                }
                if(!equals(saisie_question, "")) {
                    question.reponse = "" + saisie_question;
                }
            }
        }
        sauvegarder_qcm(qcm, id_question, question);
    }

    void supprimer_question(CSVFile qcm, int id_question) {
        println(id_question);
        sauvegarder_qcm(qcm, id_question, new Question());
    }

    void sauvegarder_qcm(CSVFile qcm, int id_question, Question question) {
        int totales_questions = rowCount(qcm);
        if(id_question == rowCount(qcm)) {
            totales_questions += 1;
        } else if(question.reponse == "-1") {
            totales_questions -= 1;
        }
        String[][] nouvelles_questions = new String[totales_questions][columnCount(qcm)];
        int nombre_questions_valides = 0;
        for(int y = 0; y < rowCount(qcm); y++) {
            if(id_question != y) {
                for(int x = 0; x < columnCount(qcm); x++) {
                    nouvelles_questions[nombre_questions_valides][x] = getCell(qcm, y, x);
                }
                nombre_questions_valides += 1;
            }
        }

        // for(int i = 0; i < columnCount(qcm); i++) {
        //     nouvelles_questions[rowCount(qcm)][i] = "";
        // }
        if(question.reponse != "-1") {
            nouvelles_questions[id_question][0] = question.reponse;
            nouvelles_questions[id_question][1] = question.question;
            nouvelles_questions[id_question][2] = question.propositions[0];
            nouvelles_questions[id_question][3] = question.propositions[1];
            nouvelles_questions[id_question][4] = question.propositions[2];
            nouvelles_questions[id_question][5] = question.propositions[3];
        }

        saveCSV(nouvelles_questions, QUESTIONS_CSV);
        println(GREEN + "Les nouvelles données ont été sauvegardé!" + RESET);
    }

    String prendre_saisie(String label) {
        println(label);
        String saisie = readString();
        while(length(saisie) > 100) {
            println(RED + "Saisie non valide." + RESET);
            print(label);
            saisie = readString();
        }
        return saisie;
    }


    // Records

    void menu_records() {
        afficher_grand_texte(TEXTE_RECORDS, YELLOW_BOLD);

        println(WHITE_BOLD + "----- RECORD POUR LE MODE NORMAL -----" + RESET);
        afficher_record(1);
        println(WHITE_BOLD + "----- RECORD POUR LE MODE DIFFICILE -----" + RESET);
        afficher_record(2);
        println(WHITE_BOLD + "----- RECORD POUR LE MODE PERSONALISEE -----" + RESET);
        afficher_record(3);


        println(WHITE_BOLD + "\n--- Appuyez sur entrée pour continuer ---" + RESET);
        readString();
    }

    // Cette méthode répète deux fois le même processus, d'abord elle affiche les records en foncton du temps, et ensuite elle affiche les records en fonction d'une difficulté.
    void afficher_record(int mode_de_jeu) {
        CSVFile qcm = loadCSV(CSV_RECORDS); // on charge le fichier

        // meilleur temps
        String nom_meilleur_temps = "";
        int temps_meilleur_temps = -1;
        int pourcentage_meilleur_temps = 0;
        for(int i = 1; i < rowCount(qcm); i++) {
            if(stringToInt(getCell(qcm, i, 3)) == mode_de_jeu) {
                if(stringToInt(getCell(qcm, i, 0)) < temps_meilleur_temps || temps_meilleur_temps == -1) {
                    temps_meilleur_temps = stringToInt(getCell(qcm, i, 0));
                    nom_meilleur_temps = getCell(qcm, i, 1);
                    pourcentage_meilleur_temps = stringToInt(getCell(qcm, i, 2));
                }
            }
        }
        if(equals(nom_meilleur_temps, "")) {
            println(BLACK + "Pas de record enregistrée!" + RESET);
        } else {
            println("Meilleur temps: " + GREEN_BOLD + temps_meilleur_temps + "s" + BLACK + " par " + WHITE + nom_meilleur_temps + YELLOW_BOLD + " (" + pourcentage_meilleur_temps +"%)" + RESET);
        }

        // maintenant on refait le même processus (en changeant des choses) mais cette fois ci pour les pourcentages.
        temps_meilleur_temps = -1;
        nom_meilleur_temps = "";
        pourcentage_meilleur_temps = -1;

        for(int i = 1; i < rowCount(qcm); i++) {
            if(stringToInt(getCell(qcm, i, 3)) == mode_de_jeu) {
                if(stringToInt(getCell(qcm, i, 2)) > pourcentage_meilleur_temps || pourcentage_meilleur_temps == -1 || stringToInt(getCell(qcm, i, 2)) == pourcentage_meilleur_temps && stringToInt(getCell(qcm, i, 0)) < temps_meilleur_temps) {
                    temps_meilleur_temps = stringToInt(getCell(qcm, i, 0));
                    nom_meilleur_temps = getCell(qcm, i, 1);
                    pourcentage_meilleur_temps = stringToInt(getCell(qcm, i, 2));
                }
            }
        }
        if(equals(nom_meilleur_temps, "")) {
            println(BLACK + "Pas de record enregistrée!" + RESET);
        } else {
            println("Meilleur pourcentage: " + GREEN_BOLD + temps_meilleur_temps + "s" + BLACK + " par " + WHITE + nom_meilleur_temps + YELLOW_BOLD + " (" + pourcentage_meilleur_temps +"%)" + RESET);
        }
        // meilleur pourcentage
    }

    void menu_aide() {
        afficher_grand_texte(TEXTE_AIDE, PURPLE_BOLD);
        println(WHITE_BOLD + "--- Bienvenue dans LavaRun! ---" + RESET);
        println("- Le but du jeu est de " + GREEN + "s'échapper du labyrinthe" + RESET + ".");
        println(GREEN_BOLD + "☻ " + RESET + "Avec votre clavier, utilisez les " + PURPLE + "touches (z/q/s/d) " + RESET + "pour se déplacer.");
        println(GREEN_BOLD + "⚑ " + RESET + "Touchez le " + GREEN + "drapeau " + RESET + " en bas à droite pour gagner la partie.");
        println("- À chaque intersection, une question vous sera posé. Si vous répondez faux, vous retournerez en arrière.");
        println(RED_BOLD + "◼ " + RESET + "De la " + RED + "lave" + RESET + " coulera quand vous vous déplacerez. Si vous la touchez, " + WHITE_BOLD + "la partie sera terminé!" + RESET);
        println(WHITE_BOLD + "\n--- Appuyez sur entrée pour continuer ---" + RESET);
        readString();
    }
}