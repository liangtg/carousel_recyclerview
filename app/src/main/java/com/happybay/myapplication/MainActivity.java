package com.happybay.myapplication;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.happybay.myapplication.carousel.CarouselLayoutManager;
import com.happybay.myapplication.carousel.CarouselZoomPostLayoutListener;
import com.happybay.myapplication.carousel.CenterScrollListener;
import java.util.Random;

public class MainActivity extends Activity {
    RecyclerView view;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.recycler);
        CarouselLayoutManager layoutManager =
            new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        view.setLayoutManager(layoutManager);
        //view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        view.setHasFixedSize(true);
        view.addOnScrollListener(new CenterScrollListener());
        view.setAdapter(new TestAdapter());
        view.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        Glide.init(this, new GlideBuilder());
    }

    private class VH extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView text;

        public VH(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.img);
        }

        @Override public void onClick(View v) {
            toast.setText(String.valueOf(getAdapterPosition()));
            toast.show();
        }
    }

    private class TestAdapter extends RecyclerView.Adapter<VH> {
        private Random random = new Random();

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.item_co, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.itemView.setBackgroundColor(random.nextInt() | 0xFF000000);
            int id = getResources().getIdentifier("i" + (position + 1), "drawable",
                getPackageName());
            Glide.with(MainActivity.this).load(id).into(holder.image);
        }

        @Override public int getItemCount() {
            return 10;
        }
    }
}
