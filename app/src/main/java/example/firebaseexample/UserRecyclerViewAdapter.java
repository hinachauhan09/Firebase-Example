package example.firebaseexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<User> list;
    private UserListener listener;

    public UserRecyclerViewAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        listener = (UserListener) context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recyclerview_child_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        final User user = list.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.mobile.setText(user.getMob());
        holder.age.setText("Age : "+ user.getAge());
       if(user.getProfile()==null || user.getProfile().isEmpty())
           Picasso.get().load(R.drawable.default_image)
                   .error(R.drawable.default_image)
                   .into(holder.image);
       else
           Picasso.get().load(user.getProfile())
                   .error(R.drawable.default_image)
                   .into(holder.image);

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(user.getId(),position);
            }
        });
    }

    public interface UserListener{
        void onButtonClicked(String userId,int position);
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, mobile, email, age;
        ImageView image, option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            mobile = itemView.findViewById(R.id.tv_mobile);
            email = itemView.findViewById(R.id.tv_email);
            age = itemView.findViewById(R.id.tv_age);
            image = itemView.findViewById(R.id.iv_profile);
            option = itemView.findViewById(R.id.iv_option);
        }
    }
}
