package com.happybay.myapplication;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.happybay.myapplication.carousel.CarouselAnimator;
import com.happybay.myapplication.carousel.CarouselLayoutManager;
import com.happybay.myapplication.carousel.CarouselSnapHelper;
import com.happybay.myapplication.carousel.CarouselZoomPostLayoutListener;

public class MainActivity extends Activity {
    RecyclerView view;
    Toast toast;
    private TestAdapter adapter;
    private CarouselLayoutManager layoutManager;
    private CarouselSnapHelper snapHelper;
    private View touchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchFilter = findViewById(R.id.touchFilter);
        touchFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ToggleButton toggle = findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleRecyclerView();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                adapter.reset();
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                view.animate()
                    .alpha(0)
                    .translationY(-view.getHeight())
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(android.animation.Animator animation) {
                            view.setAlpha(1);
                            view.setTranslationY(0);
                            adapter.array.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });
            }
        });
        view = findViewById(R.id.recycler);
        layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
        layoutManager.setMaxVisibleItems(1);
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
        snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(view);

        toggleRecyclerView();
    }

    private void toggleRecyclerView() {
        if (view.getChildCount() <= 2) return;
        int p = layoutManager.getCenterItemPosition();
        if (p > 1) {
            snapHelper.setScrollToHead(true);
            touchFilter.setEnabled(true);
            view.smoothScrollToPosition(0);
        } else {
            touchFilter.setEnabled(false);
            snapHelper.setScrollToHead(false);
            view.smoothScrollToPosition(2);
        }
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
        private int headCount = 2;

        public TestAdapter() {
            reset();
            setHasStableIds(true);
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

        @Override public int getItemViewType(int position) {
            return position < headCount ? 1 : 2;
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            if (position < headCount) {
                holder.image.setImageResource(0);
            } else {
                Glide.with(MainActivity.this)
                    .load(array.valueAt(position - headCount))
                    .into(holder.image);
            }
        }

        @Override public long getItemId(int position) {
            return position < headCount ? position : array.valueAt(position - headCount);
        }

        @Override public int getItemCount() {
            return array.size() + headCount;
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
            int animPosition = position;
            if (position < adapter.headCount) return;
            position -= adapter.headCount;
            adapter.array.removeAt(position);
            int center = layoutManager.getCenterItemPosition();
            int end = adapter.getItemCount();
            if (position == adapter.array.size() || position == adapter.array.size() - 1) {
                if (animPosition == end) {
                    adapter.notifyItemRemoved(animPosition);
                    if (center == end) {
                        adapter.notifyItemRangeChanged(0, animPosition + 1);
                    }
                } else {
                    if (position == center) {//ok
                        adapter.notifyItemRangeChanged(animPosition,
                            adapter.getItemCount() + 1);//ok
                    } else if (position == 0) {//ok
                        adapter.notifyItemRemoved(animPosition);
                    } else {//ok
                        adapter.notifyItemRemoved(animPosition);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }
            } else {
                adapter.notifyItemRangeChanged(animPosition, adapter.getItemCount());
            }
        }
    }

    class Animator extends CarouselAnimator {
        public Animator() {
            //setMoveDuration(500);
            //setAddDuration(500);
            //setChangeDuration(500);
            //setRemoveDuration(500);
        }
    }
}
