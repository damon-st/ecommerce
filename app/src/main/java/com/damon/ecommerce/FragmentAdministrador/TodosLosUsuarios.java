package com.damon.ecommerce.FragmentAdministrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class TodosLosUsuarios extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference userRef;

    private EditText texto ;
    private ImageButton botonSearch;
    private String searchInput;


    public TodosLosUsuarios() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =inflater.inflate(R.layout.fragment_todos_los_usuarios, container, false);

       recyclerView = view.findViewById(R.id.recycler_usuarios);
       recyclerView.setHasFixedSize(true);
       layoutManager = new LinearLayoutManager(getContext());
       recyclerView.setLayoutManager(layoutManager);

       userRef = FirebaseDatabase.getInstance().getReference().child("Users");

       texto = view.findViewById(R.id.texto_buscar_main_activity);
       botonSearch = view.findViewById(R.id.boton_buscar);

       botonSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               searchInput = texto.getText().toString().toLowerCase();
               onStart();
           }
       });


       return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options =new
                FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(userRef.orderByChild("name").startAt(searchInput).endAt(searchInput+ "\uf8ff"),Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,UserHolder>adapter  = new
                FirebaseRecyclerAdapter<Users, UserHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final UserHolder holder, final int i, @NonNull Users users) {

                        Picasso.get().load(users.getImage()).into(holder.users_profile_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(R.mipmap.ic_launcher).into(holder.users_profile_image);
                            }
                        });
                        holder.telefono.setText(users.getPhone());
                        holder.user_profile_name.setText(users.getName());
                        holder.user_direccion.setText(users.getAddress());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String uid = getRef(i).getKey();
                                CharSequence options[] = new CharSequence[]{
                                    "Eliminar Usuario",
                                    "No"

                            };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Escoje una de las Opciones?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which ==0){
                                            EliminarUsuario(uid);
                                        }else if (which==1){
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        UserHolder userHolder = new UserHolder(view);
                        return userHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void EliminarUsuario(String uid) {
        userRef.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class UserHolder extends RecyclerView.ViewHolder{

        public CircleImageView users_profile_image;
        public TextView user_profile_name,user_direccion;
        public TextView telefono;
        public ProgressBar progressBar;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            users_profile_image = itemView.findViewById(R.id.users_profile_image);
            user_profile_name = itemView.findViewById(R.id.user_profile_name);
            telefono = itemView.findViewById(R.id.user_status);
            user_direccion = itemView.findViewById(R.id.user_direccion);
            progressBar = itemView.findViewById(R.id.progress_admin_users);
        }
    }

}
