package example.org.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {
    private List<Photo> mPhotosList;
    private Context mContext;
    private FlickrImageViewHolder mFlickrImageViewHolder;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photosList) {
        mContext = context;
        mPhotosList = photosList;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, null);
        mFlickrImageViewHolder = new FlickrImageViewHolder(view);
        return mFlickrImageViewHolder;
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        Photo photoItem = mPhotosList.get(position);
        Picasso.with(mContext)
                .load(photoItem.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mFlickrImageViewHolder.thumbnail);
        mFlickrImageViewHolder.title.setText(photoItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return mPhotosList == null ? 0 : mPhotosList.size();
    }

   public void loadNewData(List<Photo> newPhotos){
       mPhotosList = newPhotos;
       notifyDataSetChanged();
   }
}
