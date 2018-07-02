package iyd2.projects.itunessearchapi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


public class AlbumDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_ALBUM_DATA = "iyd2.projects.itunessearchapi.album_data";

    public static Intent newIntent(Context context, String albumData) {
        Intent intent = new Intent(context, AlbumDetailActivity.class);
        intent.putExtra(EXTRA_ALBUM_DATA, albumData);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return AlbumDetailFragment.newInstance(getIntent().getStringExtra(EXTRA_ALBUM_DATA));
    }
}
