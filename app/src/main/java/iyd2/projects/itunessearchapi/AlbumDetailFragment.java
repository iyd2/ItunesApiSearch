package iyd2.projects.itunessearchapi;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Fragment class for displaying album detailed information.
 */
public class AlbumDetailFragment extends Fragment {

    private static final String TAG = "AlbumDetailFragment";
    private static final String ARG_ALBUM_ID = "album_id";
    private AlbumItem mAlbumItem;
    private ImageView mAlbumImageView;
    private TextView mAlbumTitleView;
    private TextView mArtistNameView;
    private TextView mAlbumGenreView;
    private TextView mAlbumPriceView;
    private TextView mAlbumReleasDate;
    private TextView mAlbumCopyrightView;
    private LinearLayout mSongListView;

	/**
	 * Returns AlbumDetailFragment object with album id argument in attached bundle.
	 */
    public static AlbumDetailFragment newInstance(String albumId) {
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_ID, albumId);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// Execute AsyncTask to get album information by id.
		new AlbumFetcher().execute(getArguments().getString(ARG_ALBUM_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Inflate fragment layout.
        View view = inflater.inflate(R.layout.fragment_album_detail, container, false);

		// Find views for displaying data
        mAlbumImageView = view.findViewById(R.id.album_image);
        mAlbumTitleView = view.findViewById(R.id.album_title);
        mArtistNameView = view.findViewById(R.id.artist_name);
        mAlbumGenreView = view.findViewById(R.id.genre);
        mAlbumPriceView = view.findViewById(R.id.price);
        mSongListView = view.findViewById(R.id.song_list);
        mAlbumCopyrightView = view.findViewById(R.id.copyrights);
        mAlbumReleasDate = view.findViewById(R.id.release_date);

        return view;
    }
	
	/**
	 * Set album data to views. 
	 */
    private void setData() {
		
		// Load album artwork and set it to ImageView.  
        int size = getImageSize();
        Picasso.get()
                .load(mAlbumItem.getImageUrl())
                .resize(size, size)
                .into(mAlbumImageView);

		// Set text data to TextViews. 
        mAlbumTitleView.setText(mAlbumItem.getCollectionName());
        mArtistNameView.setText(mAlbumItem.getArtistName());
        mAlbumGenreView.setText(mAlbumItem.getGenre());
        mAlbumPriceView.setText(mAlbumItem.getPriceWithCurrency());
        mAlbumCopyrightView.setText(mAlbumItem.getCopyright());
        mAlbumReleasDate.setText(mAlbumItem.getDisplayedDateString());

		// For each song initialize layout, fill it with data and add to LinearLayout.
        for (int i = 0; i < mAlbumItem.getListOfSongs().size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.list_song_item, null, false);
            TextView songNumberView = view.findViewById(R.id.song_number);
            songNumberView.setText(String.valueOf(i + 1));
            TextView songNameView = view.findViewById(R.id.song_name);
            songNameView.setText(mAlbumItem.getListOfSongs().get(i));
            mSongListView.addView(view);
        }
    }

	/**
	 * Implementation of AsyncTask to fetch album information.
	 */
    private class AlbumFetcher extends AsyncTask<String, Void, AlbumItem> {

        @Override
        protected AlbumItem doInBackground(String... strings) {
			// Return filled with data AlbumItem object by id.
            return new AlbumsFetcher().getAlbum(strings[0]);
        }

		// Executes in main thread.
        @Override
        protected void onPostExecute(AlbumItem item) {
			if (item == null) {
				getActivity().finish();
			}
			
			// Save link on returned object and set its data to views.
            mAlbumItem = item;
            setData();
        }
    }
	
	/**
	 * Returns image side size.
	 */
    private int getImageSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = displayMetrics.widthPixels / 3;
        return size;
    }
}
