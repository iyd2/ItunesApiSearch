package iyd2.projects.itunessearchapi;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumsRecyclerFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int mImageSize;

    public static AlbumsRecyclerFragment newInstance() {

        Bundle args = new Bundle();

        AlbumsRecyclerFragment fragment = new AlbumsRecyclerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        new AlbumSearchTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_recycler, container, false);

        Resources res = getResources();

        int spacingInPixels = (int) res.getDimension(R.dimen.item_margin);
        int spanCount = res.getInteger(R.integer.span_count);

        //
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mImageSize = (displayMetrics.widthPixels  - (spanCount + 1) * spacingInPixels) / spanCount;
        //

        mRecyclerView = view.findViewById(R.id.albums_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.addItemDecoration(new ItemDecorator(2, spacingInPixels));
        mRecyclerView.setAdapter(new AlbumsAdapter());

        return view;
    }

    private class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AlbumItem mAlbumItem;
        private ImageView mAlbumImageView;
        private TextView mAlbumTitleView;
        private TextView mAlbumArtistView;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAlbumImageView = itemView.findViewById(R.id.album_image);
            mAlbumTitleView = itemView.findViewById(R.id.album_title);
            mAlbumArtistView = itemView.findViewById(R.id.album_artist);
        }

        public void onBindAlbumsItem(AlbumItem albumItem) {
            mAlbumItem = albumItem;
            mAlbumTitleView.setText(albumItem.getCollectionName());
            mAlbumArtistView.setText(albumItem.getArtistName());

            Picasso.get()
                    .load(mAlbumItem.getImageUrl())
                    .resize(mImageSize, mImageSize)
                    .into(mAlbumImageView);
        }

        @Override
        public void onClick(View view) {
            startActivity(AlbumDetailActivity.newIntent(getActivity(), mAlbumItem.getCollectionId()));
        }
    }

    private class AlbumsAdapter extends RecyclerView.Adapter<AlbumHolder> {
        private SortedList<AlbumItem> mAlbums;

        public AlbumsAdapter() {
            mAlbums = new SortedList<>(AlbumItem.class, new SortedList.Callback<AlbumItem>() {
                @Override
                public int compare(AlbumItem albumItem, AlbumItem t21) {
                    return albumItem.compareTo(t21);
                }

                @Override
                public void onChanged(int i, int i1) {
                    notifyItemRangeChanged(i, i1);
                }

                @Override
                public boolean areContentsTheSame(AlbumItem albumItem, AlbumItem t21) {
                    return albumItem.equals(t21);
                }

                @Override
                public boolean areItemsTheSame(AlbumItem albumItem, AlbumItem t21) {
                    return albumItem.equals(t21);
                }

                @Override
                public void onInserted(int i, int i1) {
                    notifyItemRangeInserted(i1, i1);
                }

                @Override
                public void onRemoved(int i, int i1) {
                    notifyItemRangeRemoved(i, i1);
                }

                @Override
                public void onMoved(int i, int i1) {
                    notifyItemMoved(i, i1);
                }
            });
        }

        public void addAll(List<AlbumItem> albums) {
            mAlbums.beginBatchedUpdates();
            mAlbums.addAll(albums);
            mAlbums.endBatchedUpdates();
        }

        public void replaceAll(List<AlbumItem> albums) {
            mAlbums.beginBatchedUpdates();
            mAlbums.replaceAll(albums);
            mAlbums.endBatchedUpdates();
        }

        public boolean isEmpty() {
            return  mAlbums.size() == 0;
        }

        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_album_item, viewGroup, false);
            return new AlbumHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder albumHolder, int i) {
            albumHolder.onBindAlbumsItem(mAlbums.get(i));
        }

        @Override
        public int getItemCount() {
            return mAlbums.size();
        }
    }

    private class ItemDecorator extends RecyclerView.ItemDecoration {
        private int mSpanCount;
        private int mSpace;

        public ItemDecorator(int spanCount, int space) {
            mSpanCount = spanCount;
            mSpace = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            //super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            int column = position % mSpanCount;

            outRect.left = mSpace - column * mSpace / mSpanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mSpace / mSpanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < mSpanCount) {
                outRect.top = mSpace;
            }
            outRect.bottom = mSpace;
        }
    }

    private class AlbumSearchTask extends AsyncTask<Void, Void, List<AlbumItem>> {

        @Override
        protected List<AlbumItem> doInBackground(Void... voids) {
            return new AlbumsFetcher().searchAlbums();
        }

        @Override
        protected void onPostExecute(List<AlbumItem> albumItems) {
            AlbumsAdapter adapter = (AlbumsAdapter) mRecyclerView.getAdapter();

            if (adapter.isEmpty()) {
                adapter.addAll(albumItems);
            } else {
                adapter.replaceAll(albumItems);
            }
        }
    }

}
