package com.example.moviesbook;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Book;
import com.example.moviesbook.Movie;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

public class mutuals
{
    private String id;
    private String id2;
    private ArrayList<Book> mutualbooks;
    private ArrayList<Movie> mutualmovies;
    Context context;
    private int numberofmutualbooks;
    private int numberofmutualmovies;
    private FirebaseFirestore db;

    public mutuals(String id, String id2, FirebaseFirestore db,Context cont)
    {
        this.id = id;
        this.id2 = id2;
        context = cont;
        this.db = db;
        mutualbooks = new ArrayList<>();
        mutualmovies = new ArrayList<>();

    }


    public int getNumberofmutualbooks()
    {
        return mutualbooks.size();
    }

    public int getNumberofmutualmovies()
    {
        return mutualmovies.size();
    }



    public ArrayList<Movie> getMutualmovies()
    {
        this.mutualmovies = getMovies(id, id2);
        return this.mutualmovies;
    }
    public ArrayList<Book> getMutualbooks()
    {
        this.mutualbooks = getBooks(id, id2);
        return this.mutualbooks;
    }

    public ArrayList<Movie> getMovies(final String id, final String id2)
    {
        final ArrayList<Movie> movies = new ArrayList<>();
        final Set<String> repeated = new HashSet<String>();

        CollectionReference ref = db.collection("Movies");
        Task task1 = ref.whereArrayContains("users", id).get();

        Task task2 = ref.whereArrayContains("users", id2).get();

        Task<java.util.List<QuerySnapshot>> alltasks = Tasks.whenAllSuccess(task1, task2);
        alltasks.addOnSuccessListener(new OnSuccessListener<java.util.List<QuerySnapshot>>() {
            @Override
            public void onSuccess(java.util.List<QuerySnapshot> querySnapshots)
            {

                int x = 0;
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots)
                {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    {

                        Movie movie = snapshot.toObject(Movie.class);
                        if(repeated.contains(snapshot.getId())) {
                            movies.add(movie);
                            x++;
                        }
                        else
                            repeated.add(snapshot.getId());
                    }

                }

            }

        });
        Toast.makeText(context,"onCreate: mutual movies____________ :" + String
                .valueOf(movies.size()),Toast.LENGTH_LONG).show();
        return movies;
    }
    public ArrayList<Book> getBooks(final String id, final String id2)
    {
        final ArrayList<Book> books = new ArrayList<>();
        final Set <String> repeated = new HashSet<String>();

        CollectionReference ref = db.collection("Books");
        Task task1 = ref.whereArrayContains("users", id).get();

        Task task2 = ref.whereArrayContains("users", id2).get();

        Task<java.util.List<QuerySnapshot>> alltasks = Tasks.whenAllSuccess(task1, task2);
        alltasks.addOnSuccessListener(new OnSuccessListener<java.util.List<QuerySnapshot>>() {
            @Override
            public void onSuccess(java.util.List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots)
                {
                    int x = 0;
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    {
                        Book book = snapshot.toObject(Book.class);
                        if(repeated.contains(book.getID())) {
                            books.add(book);
                            x++;
                        }
                        else
                            repeated.add(book.getID());

                    }
                }
            }
        });



        return  books;

    }


}