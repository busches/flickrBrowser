package example.org.flickrbrowser;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetFlickrJsonData extends GetRawData {
    private final String LOG_TAG = GetFlickrJsonData.class.getSimpleName();
    private List<Photo> mPhotos;
    private Uri mDestinationUri;

    public GetFlickrJsonData(String searchCriteria, boolean matchAll) {
        super(null);
        createAndUpdateUri(searchCriteria, matchAll);
        mPhotos = new ArrayList<Photo>();
    }

    @Override
    public void execute() {
        setRawUrl(mDestinationUri.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.d(LOG_TAG, "Built URI " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    private boolean createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKER_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAG_MODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        mDestinationUri = Uri.parse(FLICKER_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAG_MODE_PARAM, matchAll ? "all" : "any")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();
        return mDestinationUri != null;
    }

    private void processResult() {
        if (getDownloadStatus() != DownloadStatus.OK) {
            Log.d(LOG_TAG, "Error downloading rarw file");
            return;
        }

        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_AUTHOR_ID = "author_id";
        final String FLICKR_LINK = "link";
        final String FLICKR_TAGS = "tags";

        try {
            JSONObject jsonData = new JSONObject(getData());
            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);
            for (int itemNumber = 0; itemNumber < itemsArray.length(); itemNumber++) {
                JSONObject jsonPhoto = itemsArray.getJSONObject(itemNumber);
                String title = jsonPhoto.getString(FLICKR_TITLE);
                String author = jsonPhoto.getString(FLICKR_AUTHOR);
                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                String link = jsonPhoto.getString(FLICKR_LINK);
                String tags = jsonPhoto.getString(FLICKR_TAGS);
                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL);

                Photo photo = new Photo(title, author, authorId, link, tags, photoUrl);
                this.mPhotos.add(photo);
            }

            for (Photo singlePhoto : mPhotos) {
                Log.d(LOG_TAG, singlePhoto.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error processing JSON data", e);
        }
    }

    public class DownloadJsonData extends DownloadRawData {
        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] param = {mDestinationUri.toString()};
            return super.doInBackground(param);
        }
    }
}
