package iyd2.projects.itunessearchapi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Activity class for hosting detail AlbumDetailActivity.
 */
public class AlbumDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_ALBUM_ID = "iyd2.projects.itunessearchapi.album_id";

	/**
	 * Returns intent object for starting activity
	 * with album id param.
	 */
    public static Intent newIntent(Context context, String albumId) {
        Intent intent = new Intent(context, AlbumDetailActivity.class);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        return intent;
    }
	
	/**
	 * Returns fragment object, that will be hosted in activity.
	 * Passes album id param in fragment.
	 */
    @Override
    protected Fragment createFragment() {
        return AlbumDetailFragment.newInstance(getIntent().getStringExtra(EXTRA_ALBUM_ID));
    }
}
