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

public class AlbumDetailFragment extends Fragment {

    private static final String TAG = "AlbumDetailFragment";
    private static final String ARG_ALBUM_DATA = "album_data";
    private AlbumItem mAlbumItem;
    private ImageView mAlbumImageView;
    private TextView mAlbumTitleView;
    private TextView mArtistNameView;
    private TextView mAlbumGenreView;
    private TextView mAlbumPriceView;

    private TextView mAlbumCopyrightView;
    private LinearLayout mSongListView;

    public static AlbumDetailFragment newInstance(String albumData) {
        
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_DATA, albumData);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mAlbumItem = new AlbumItem(getArguments().getString(ARG_ALBUM_DATA));
       // mAlbumItem.fillFromJsonFull();
        new SongsFetcher().execute(getArguments().getString(ARG_ALBUM_DATA));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_detail, container, false);

        mAlbumImageView = view.findViewById(R.id.album_image);

        mAlbumTitleView = view.findViewById(R.id.album_title);


        mArtistNameView = view.findViewById(R.id.artist_name);


        mAlbumGenreView = view.findViewById(R.id.genre);


        mAlbumPriceView = view.findViewById(R.id.price);

        mSongListView = view.findViewById(R.id.song_list);

        mAlbumCopyrightView = view.findViewById(R.id.copyrights);


        return view;
    }

    private void setData() {
        int size = getImageSize();

        Picasso.get()
                .load(mAlbumItem.getImageUrl())
                .resize(size, size)
                .into(mAlbumImageView);

        mAlbumTitleView.setText(mAlbumItem.getCollectionName());
        mArtistNameView.setText(mAlbumItem.getArtistName());
        mAlbumGenreView.setText(mAlbumItem.getGenre());
        mAlbumPriceView.setText(mAlbumItem.getPrice() + " " + mAlbumItem.getCurrency());
        mAlbumCopyrightView.setText(mAlbumItem.getCopyright());

        for (int i = 0; i < mAlbumItem.getListOfSongs().size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.list_song_item, null, false);
            TextView songNumberView = view.findViewById(R.id.song_number);
            songNumberView.setText(String.valueOf(i + 1));
            TextView songNameView = view.findViewById(R.id.song_name);
            songNameView.setText(mAlbumItem.getListOfSongs().get(i));
            mSongListView.addView(view);
        }


    }



    private class SongsFetcher extends AsyncTask<String, Void, AlbumItem> {

        @Override
        protected AlbumItem doInBackground(String... strings) {
            return new AlbumsFetcher().getAlbum(strings[0]);
        }

        @Override
        protected void onPostExecute(AlbumItem item) {
            mAlbumItem = item;
            setData();
        }
    }

    private int getImageSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = displayMetrics.widthPixels / 3;
        return size;
    }
}
