package td3.td1;

import td3.td1.commandes.Produit;
import td3.td1.commandes.Categorie;
import td3.td1.commandes.Commande;
import td3.td1.commandes.DAO;
import td3.td1.paires.Paire;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class App1 {
    public static final Predicate<Produit> produitATVAReduite = p -> p.cat().equals(Categorie.REDUIT);

    public static final Predicate<Paire<Produit, Integer>> paireTVAReduite = paire -> produitATVAReduite
            .test(paire.fst());

    public static final Predicate<Paire<Produit, Integer>> genPredicate(int nombreMinimunDAchat) {
        return
                // new Predicate<Paire<Produit, Integer>>() {
                // @Override
                // public boolean test(Paire<Produit, Integer> p) {
                // return p.snd().intValue() > nombreMinimunDAchat;
                // }
                // };
                p -> p.snd().intValue() > nombreMinimunDAchat;
    }

    public static final Function<Paire<Produit, Integer>, Double> calcul1 = ligneDeCommande -> {
        final Produit produit = ligneDeCommande.fst();
        final double prix_unitaire = produit.prix();
        final double tva = produit.cat().tva();
        final int qte = ligneDeCommande.snd().intValue();
        return prix_unitaire * (1+tva) * qte;
    };

    public static final Function<Double,Double> reduction = v -> v *  0.8;

    public static final Function<Paire<Produit, Integer>, Double> calcul2 = ligneDeCommande -> {
        final Produit produit = ligneDeCommande.fst();
        final double prix_unitaire = produit.prix();
        final double tva = produit.cat().tva();
        final int qte = ligneDeCommande.snd().intValue();
        if (qte > 2) {
            return calcul1.andThen(reduction).apply(ligneDeCommande);
        } else {
            return calcul1.apply(ligneDeCommande);
        }
    };

    private static final void q1() {
        DAO db = DAO.instance();
        // produits à TVA réduite
        Set<Produit> v1 = db.selectionProduits(produitATVAReduite);
        System.out.println(v1);
        // produits à TVA réduite coûtant plus de 5 EUR
        Set<Produit> v2 = db.selectionProduits(produitATVAReduite.and(p -> p.prix() > 5.0));
        System.out.println(v2);
        // commandes non normalisées de + de 2 items
        List<Commande> v3 = db.selectionCommande(cde -> cde.lignes().size() > 2);
        System.out.println(v3);
        // commmandes ayant au - un produit à TVA réduite commandé en plus de 2
        // exemplaires
        List<Commande> v4 = db
                .selectionCommandeSurExistanceLigne(paire -> paireTVAReduite.and(genPredicate(2)).test(paire));
        System.out.println(v4);
        // afficher les commandes (pas avec toString)
        for (Commande cde : db.commandes()) {
            cde.affiche(calcul1);
        }
        // idem mais avec réduction du p.u. quand la qté > 2
        for (Commande cde : db.commandes()) {
            cde.affiche(calcul2);
        }
    }

    public static void main(String[] args) {
        q1();
    }
}


