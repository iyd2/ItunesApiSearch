package iyd2.projects.itunessearchapi;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Launch activity.
 * Activity class for hosting AlbumsRecyclerFragment.
 */
public class AlbumsRecyclerActivity extends SingleFragmentActivity {

	/**
	 * Returns fragment object, that will be hosted in activity.
	 */
    @Override
    protected Fragment createFragment() {
        return AlbumsRecyclerFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
