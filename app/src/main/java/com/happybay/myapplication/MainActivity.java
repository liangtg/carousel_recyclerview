package com.happybay.myapplication;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.happybay.myapplication.carousel.CarouselAnimator;
import com.happybay.myapplication.carousel.CarouselLayoutManager;
import com.happybay.myapplication.carousel.CarouselSnapHelper;
import com.happybay.myapplication.carousel.CarouselZoomPostLayoutListener;
import java.util.ArrayList;

public class MainActivity extends Activity {
    RecyclerView view;
    Toast toast;
    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                adapter.reset();
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                adapter.notifyItemRangeRemoved(0, adapter.array.size());
                adapter.array.clear();
            }
        });
        view = findViewById(R.id.recycler);
        CarouselLayoutManager layoutManager =
            new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
        adapter = new TestAdapter();
        view.setAdapter(adapter);
        view.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        Glide.init(this, new GlideBuilder());
        ItemTouchHelper helper = new ItemTouchHelper(new DeleteHelper());
        helper.attachToRecyclerView(view);
        view.setItemAnimator(new Animator());
        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(view);
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
        private SparseIntArray array = new SparseIntArray(10);

        public TestAdapter() {
            reset();
        }

        private void reset() {
            array.clear();
            for (int i = 0; i < 10; i++) {
                array.append(i, getResources().getIdentifier("i" + (i + 1), "drawable",
                    getPackageName()));
            }
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.item_co, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Glide.with(MainActivity.this).load(array.valueAt(position)).into(holder.image);
        }

        @Override public int getItemCount() {
            return array.size();
        }
    }

    private class DeleteHelper extends ItemTouchHelper.Callback {

        @Override public int getMovementFlags(@NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        }

        @Override public boolean onMove(@NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.array.removeAt(position);
            adapter.notifyItemRemoved(position);
            if (adapter.array.size() == 0) return;
            if (position == adapter.array.size()) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeChanged(position, 2);
            }
        }
    }

    class Animator extends CarouselAnimator {
        public Animator() {
            setRemoveDuration(500);
            setMoveDuration(500);
            setAddDuration(500);
            setChangeDuration(500);
        }
        ArrayList<RecyclerView.ViewHolder> list = new ArrayList<>();
        float scale;

        @Override public void onChangeStarting(RecyclerView.ViewHolder item, boolean oldItem) {
            if (list.contains(item)){
                list.remove(item);
                return;
            }
            Log.d("anim", "pos:" + oldItem+item.getAdapterPosition());
            list.add(item);
            if (oldItem) {
                scale = item.itemView.getScaleX();
                item.itemView.animate().cancel();
            }else{
                float nscale = item.itemView.getScaleX();
                item.itemView.setScaleX(scale);
                item.itemView.setScaleY(scale);
                item.itemView.animate().scaleX(nscale).scaleY(nscale).start();
            }
        }
    }
}
