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
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Fragment class for displaying found items.
 */
public class AlbumsRecyclerFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int mImageSize;

	/**
	 * Returns AlbumsRecyclerFragment object.
	 */
    public static AlbumsRecyclerFragment newInstance() {
        AlbumsRecyclerFragment fragment = new AlbumsRecyclerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Search request on value stored in SharedPreferences.
        // Uses to display data after return by toolbar navigation button
        // and to fill fragment with albums when there weren't search request.
        new AlbumSearchTask().execute(QueryPreferences.getStoredQuery(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_recycler, container, false);

        Resources res = getResources();
		
		// Calculate image size.
        int spacingInPixels = (int) res.getDimension(R.dimen.item_margin);
        int spanCount = res.getInteger(R.integer.span_count);
        mImageSize = getImageSize(spacingInPixels, spanCount);

		// Initialize RecyclerView object.
		// Set layout manager, decorator and adapter.
        mRecyclerView = view.findViewById(R.id.albums_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.addItemDecoration(new ItemDecorator(2, spacingInPixels));
        mRecyclerView.setAdapter(new AlbumsAdapter());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate menu.
        inflater.inflate(R.menu.albums_recycler_menu, menu);

        // Find searchView, and set onSubmit listener.
        final MenuItem searchItem = menu.findItem(R.id.albums_search_view);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Store current query in SharedPreferences and execute query.
                QueryPreferences.setStoredQuery(getContext(), query);
                new AlbumSearchTask().execute(query);
                searchView.clearFocus(); // Close keyboard.
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.menu.albums_recycler_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
	 * RecyclerView.ViewHolder implementation.
	 */
    private class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AlbumItem mAlbumItem;
        private ImageView mAlbumImageView;
        private TextView mAlbumTitleView;
        private TextView mAlbumArtistView;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this); // Set OnClickListener on item view.
			
			// Find item layout views.
            mAlbumImageView = itemView.findViewById(R.id.album_image);
            mAlbumTitleView = itemView.findViewById(R.id.album_title);
            mAlbumArtistView = itemView.findViewById(R.id.album_artist);
        }
		
		/**
		 * Fill views with album data.
		 * Calling in RecyclerView.Adapter.
		 */
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
			// On click on item view start AlbumDetailActivity.
			// Passes id of current album.
            startActivity(AlbumDetailActivity.newIntent(getActivity(), mAlbumItem.getCollectionId()));
        }
    }
	
	/**
	 * RecyclerView.Adapter implementation.
	 */
    private class AlbumsAdapter extends RecyclerView.Adapter<AlbumHolder> {
        private SortedList<AlbumItem> mAlbums;

        public AlbumsAdapter() {
			// SortedList realization.Uses to keep elements sorted
			// and automatic recyclerView notifying on data changing. 
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
                    notifyItemRangeInserted(i, i1);
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
		
		/**
		 * Add elements.
		 */
        public void addAll(List<AlbumItem> albums) {
            mAlbums.beginBatchedUpdates();
            mAlbums.addAll(albums);
            mAlbums.endBatchedUpdates();
        }
		
		/**
		 * Replace elements.
		 */
        public void replaceAll(List<AlbumItem> albums) {
            mAlbums.beginBatchedUpdates();
            mAlbums.replaceAll(albums);
            mAlbums.endBatchedUpdates();
        }
		
		/**
		 * Check if list is empty.
		 */
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
            // Pass current Album object in ViewHolder.
            albumHolder.onBindAlbumsItem(mAlbums.get(i));
        }

        @Override
        public int getItemCount() {
            return mAlbums.size();
        }
    }

	/**
	 * RecyclerView.Adapter implementation.
	 */
    private class ItemDecorator extends RecyclerView.ItemDecoration {
        private int mSpanCount;
        private int mSpace;

        public ItemDecorator(int spanCount, int space) {
            mSpanCount = spanCount;
            mSpace = space;
        }
		
		/**
		 * Sets item offsets to make margins between them.
		 */
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
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

	/**
	 * Implementation of AsyncTask to search albums information.
	 */
    private class AlbumSearchTask extends AsyncTask<String, Void, List<AlbumItem>> {

        @Override
        protected List<AlbumItem> doInBackground(String... strings) {
			// Returns found albums by query.
            return new AlbumsFetcher().searchAlbums(strings[0]);
        }
		
		// Executes in main thread.
        @Override
        protected void onPostExecute(List<AlbumItem> albumItems) {
            AlbumsAdapter adapter = (AlbumsAdapter) mRecyclerView.getAdapter();
			
			// If adapter list is empty then add all albums.
			// Else replace all albums.
            if (adapter.isEmpty()) {
                adapter.addAll(albumItems);
            } else {
                adapter.replaceAll(albumItems);
                mRecyclerView.smoothScrollToPosition(0); // If user is on middle of list.
            }
        }
    }
	
	/**
	 * Returns image side size.
	 */
	private int getImageSize(int spacingInPixels, int spanCount) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (displayMetrics.widthPixels  - (spanCount + 1) * spacingInPixels) / spanCount;
	}

}
