package iyd2.projects.itunessearchapi;

import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for working with itunes search api.
 */
public class AlbumsFetcher {
    private static final String TAG = "AlbumsFetcher";
    private final String ENDPOINT = "https://itunes.apple.com/";
    private final String SEARCH = "search?media=music&entity=album";
    private final String LOOKUP = "lookup?media=music";
    private String defaultQuery = "Michal Jackson";
	
	/**
	 * Returns AlbumItem object with full detail information.
	 */
    public AlbumItem getAlbum(String collectionId) {
        try {
			// Build url to request album information and its songs.
            String urlSpec = Uri.parse(ENDPOINT + LOOKUP)
                    .buildUpon()
                    .appendQueryParameter("entity", "song")
                    .appendQueryParameter("id", collectionId)
                    .build()
                    .toString();
            Log.d(TAG, urlSpec);
            JSONObject response = new JSONObject(getUrlString(urlSpec));
			
			// Return initialized from json response object.  
            return parseAlbum(response);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
	
	/**
	 * Parse json response to initialize AlbumItem object.
	 * Returns initialized AlbumItem object.
	 */
	public AlbumItem parseAlbum(JSONObject response) {
        try {
            JSONArray albumJsons = response.getJSONArray("results"); // "Results" property contains array of objects.
            JSONObject albumJSON = albumJsons.getJSONObject(0); // First object in json array contains detailed album information.

			// As long as there is no url to artwork with bigger dimensions in response, build it here.   
            String imageUrl = albumJSON.getString("artworkUrl100").replace("100x100bb", "600x600bb"); 
			
			// Create AlbumItem object.
            AlbumItem albumItem = new AlbumItem(albumJSON.getString("collectionId"),
                    albumJSON.getString("collectionName"),
                    albumJSON.getString("artistName"),
                    imageUrl);

			// Fill AlbumItem object with all existing data in json response.
            if (albumJSON.has("collectionPrice")) albumItem.setPrice(albumJSON.getString("collectionPrice"));
            if (albumJSON.has("currency")) albumItem.setCurrency(albumJSON.getString("currency"));
            if (albumJSON.has("contentAdvisoryRating")) albumItem.setContentAdvisoryRating(albumJSON.getString("contentAdvisoryRating"));
            if (albumJSON.has("collectionPrice")) albumItem.setCollectionExplicitness(albumJSON.getString("collectionExplicitness"));
            if (albumJSON.has("collectionExplicitness")) albumItem.setTrackCount(albumJSON.getString("trackCount"));
            if (albumJSON.has("trackCount")) albumItem.setCopyright(albumJSON.getString("copyright"));
            if (albumJSON.has("collectionPrice")) albumItem.setCountry(albumJSON.getString("country"));
            if (albumJSON.has("country")) albumItem.setGenre(albumJSON.getString("primaryGenreName"));
            if (albumJSON.has("releaseDate")) albumItem.setReleaseDate(albumJSON.getString("releaseDate"));
            if (albumJSON.has("collectionPrice")) albumItem.setListOfSongs(parseSongs(response)); // Other objects in json array describes album songs information.

            return albumItem;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
	
	/**
	 * Returns List of AlbumItem objects on search request.
	 */
    public List<AlbumItem> searchAlbums(String query) {
        try {
			// Build url to request list of albums.
            String urlSpec = Uri.parse(ENDPOINT + SEARCH)
                .buildUpon()
                .appendQueryParameter("term", query)
                .build()
                .toString();
            JSONObject response = new JSONObject(getUrlString(urlSpec));
			// Return list of parsed albums from json response. 
            return parseAlbums(response);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<AlbumItem> parseAlbums(JSONObject response) {
        List<AlbumItem> albumItems = new ArrayList<>();
        try {
            JSONArray albumJsons = response.getJSONArray("results"); // "Results" property contains array of objects.
			
			// Creating AlbumItem objects for each object from json response.
            for (int i = 0; i < albumJsons.length(); i ++) {
                JSONObject albumJson = albumJsons.getJSONObject(i);
				
				// If json object hasn't got one of main attributes then skip it.
				if (!albumJson.has("artworkUrl100")  ||
					!albumJson.has("collectionId")   ||
					!albumJson.has("collectionName") ||
					!albumJson.has("artistName")) {
					continue;
				}
				
				// As long as there is no url to artwork with bigger dimensions in response, build it here. 
                String imageUrl = albumJson.getString("artworkUrl100").replace("100x100bb", "600x600bb");
				if (!isUrlValid(imageUrl)) {
					continue;
				}
				
				// Create AlbumItem object and put it to return list.
				AlbumItem albumItem = new AlbumItem(albumJson.getString("collectionId"),
                        albumJson.getString("collectionName"),
                        albumJson.getString("artistName"),
                        imageUrl);
				
                albumItems.add(albumItem);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return albumItems;
    }

	/**
	 * Parse json response to get list of album songs.
	 * Returns List objects.
	 */
    public List<String> parseSongs(JSONObject response) {
        List<String> songs = new ArrayList<>();
        
		try {
            JSONArray songJsons = response.getJSONArray("results"); // "Results" property contains array of objects.

			// Adding song name to return list for each object from json response.
            for (int i = 1; i < songJsons.length(); i++) {
                JSONObject songJson = songJsons.getJSONObject(i);
				
				// If current object is not a song then skip it.
                if (!songJson.getString("wrapperType").equals("track")) {
                    continue;
                }
				
				// If current object hasn't got a "trackName" property then skip this object.
                if (!songJson.has("trackName")) {
					continue;
				}

				String songName = songJson.getString("trackName");

                songs.add(songName);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return songs;
    }

	/**
	 * Returns String response on given url.
	 */
    public String getUrlString (String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

	/**
	 * Returns byte array response on given url.
	 */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream in = urlConnection.getInputStream()){

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            return out.toByteArray();

        } finally {
            urlConnection.disconnect();
        }
    }
	
	/**
	 * Checks if given url is valid.
	 */
	private boolean isUrlValid(String urlSpec) {
        try {
            new URL(urlSpec);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
