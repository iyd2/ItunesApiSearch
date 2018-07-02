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

public class AlbumsFetcher {
    private static final String TAG = "AlbumsFetcher";
    private final String ENDPOINT = "https://itunes.apple.com/";
    private final String SEARCH = "search?media=music&entity=album";
    private final String LOOKUP = "lookup?media=music";

    public AlbumItem getAlbum(String collectionId) {
        try {
            String urlSpec = Uri.parse(ENDPOINT + LOOKUP)
                    .buildUpon()
                    .appendQueryParameter("entity", "song")
                    .appendQueryParameter("id", collectionId)
                    .build()
                    .toString();

            Log.d(TAG, urlSpec);
            JSONObject response = new JSONObject(getUrlString(urlSpec));

            return parseAlbum(response);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public List<AlbumItem> searchAlbums() {
        try {
            String urlSpec = Uri.parse(ENDPOINT + SEARCH)
                .buildUpon()
                .appendQueryParameter("term", "green day")
                .build()
                .toString();
            JSONObject response = new JSONObject(getUrlString(urlSpec));
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
            JSONArray albumJsons = response.getJSONArray("results");

            for (int i = 0; i < albumJsons.length(); i ++) {
                JSONObject albumJson = albumJsons.getJSONObject(i);
                AlbumItem albumItem = new AlbumItem(albumJson.toString());
                albumItem.fillFromJson();
                albumItems.add(albumItem);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return albumItems;
    }

    public AlbumItem parseAlbum(JSONObject response) {
        try {
            JSONArray albumJsons = response.getJSONArray("results");

            JSONObject albumJSON = albumJsons.getJSONObject(0);

            String imageUrl = albumJSON.getString("artworkUrl60").replace("60x60bb", "600x600bb");


            AlbumItem albumItem = new AlbumItem(albumJSON.getString("collectionId"),
                    albumJSON.getString("collectionName"),
                    albumJSON.getString("artistName"),
                    imageUrl);

            albumItem.setPrice(albumJSON.getInt("collectionPrice"));
            albumItem.setCurrency(albumJSON.getString("currency"));
            albumItem.setContentAdvisoryRating(albumJSON.getString("contentAdvisoryRating"));
            albumItem.setCollectionExplicitness(albumJSON.getString("collectionExplicitness"));
            albumItem.setTrackCount(albumJSON.getInt("trackCount"));
            albumItem.setCopyright(albumJSON.getString("copyright"));
            albumItem.setCountry(albumJSON.getString("country"));
            albumItem.setGenre(albumJSON.getString("primaryGenreName"));
            albumItem.setListOfSongs(parseSongs(response));

            return albumItem;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public List<String> parseSongs(JSONObject response) {
        List<String> songs = new ArrayList<>();
        try {
            JSONArray songJsons = response.getJSONArray("results");

            for (int i = 1; i < songJsons.length(); i++) {
                JSONObject songJson = songJsons.getJSONObject(i);

                if (!songJson.getString("wrapperType").equals("track")) {
                    continue;
                }

                String songName = songJson.getString("trackName");

                if (songName == null) {
                    continue;
                }

                songs.add(songName);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return songs;
    }

    public String getUrlString (String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

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
}
