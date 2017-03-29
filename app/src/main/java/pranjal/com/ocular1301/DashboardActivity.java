package pranjal.com.ocular1301;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;

import static android.R.attr.drawable;
import static android.R.attr.numberPickerStyle;
import static android.R.attr.shouldDisableView;
import static pranjal.com.ocular1301.R.layout.popup_action;

public class DashboardActivity extends AppCompatActivity {
    private Intent mShareIntent;
    File file;
    Uri uri;
    private ProgressBar bar;
private int position;
    private GridLayoutManager lLayout;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dashboard");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.refresh);
        actionBar.show();
        getFromSdcard();
        lLayout = new GridLayoutManager(getApplicationContext(), 2);




        RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getApplicationContext(), f);
        rView.setAdapter(rcAdapter);

    }
    public void getFromSdcard()
    {
         file= new File(android.os.Environment.getExternalStorageDirectory(),"Ocular");

        if (file.isDirectory())
        {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {

                f.add(listFile[i].getAbsolutePath());

            }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
        private ArrayList<String> f;
        private Context context;

        public RecyclerViewAdapter(Context applicationContext, ArrayList<String> f) {
            this.f=f;
            this.context=applicationContext;
        }

        @Override
        public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, null);
            RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);

            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolders holder, int position) {
            holder.title.setText(listFile[position].getName());
            if (f.get(position).endsWith("pdf")) {
                Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf_thumbnail), 150, 200);
                holder.imageview.setImageBitmap(bm);
            } else {
                Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.get(position)), 150, 200);
                holder.imageview.setImageBitmap(bm);
            }

        }
        @Override
        public int getItemCount() {
            return f.size();
        }

    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener, PopupInflaterListener {

        public TextView title;
        public ImageView imageview;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageview= (ImageView)itemView.findViewById(R.id.thumbnail);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uri=Uri.fromFile(listFile[getPosition()]);

                    if(listFile[getPosition()].getName().endsWith("pdf")){
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(Uri.fromFile(listFile[getPosition()]),"application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    }else {
                        LongPressPopup(view);

                    }
                }
            });
            title = (TextView)itemView.findViewById(R.id.title);

        }

        @Override
        public void onClick(View view) {
            uri=Uri.fromFile(listFile[getPosition()]);

            if(listFile[getPosition()].getName().endsWith("pdf")){
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(listFile[getPosition()]),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }
            }else {
                LongPressPopup(view);

            }
        }

        public void LongPressPopup(View view){
            LongPressPopup popup = new LongPressPopupBuilder(DashboardActivity.this)// A Context object for the builder constructor
                    .setTarget(view)// The View which will open the popup if long pressed
            .setLongPressDuration(1)
                    .setPopupView(R.layout.popup_action,this)// The View to show when long pressed
                    .setDismissOnLongPressStop(false)
                    .setDismissOnTouchOutside(true)
                    .setDismissOnBackPressed(true)
                    .setCancelTouchOnDragOutsideView(true)
                    .setLongPressReleaseListener(this)
                    .setTag("PopupFoo")
                    .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER).build();
            popup.register();
        }
        @Override
        public void onViewInflated(@Nullable String popupTag, final View root) {
ImageView iv=(ImageView)root.findViewById(R.id.dialog_image);
            final RelativeLayout editable=(RelativeLayout)root.findViewById(R.id.editables);
            iv.setImageURI(uri);
            final EditText editName=(EditText) root.findViewById(R.id.edit_name);
            final Button ok=(Button)root.findViewById(R.id.ok);
            Button share=(Button)root.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareDocument();
                }
            });
            Button rename=(Button)root.findViewById(R.id.Rename);
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    editable.setVisibility(View.VISIBLE);
                    editName.setFocusable(true);
               ok.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
editable.setVisibility(View.INVISIBLE);
                       //Rename function here
if(listFile[getPosition()].getName().endsWith("pdf")) {
    listFile[getPosition()].renameTo(new File(file, "" + editName.getText() + ".pdf"));
}else {
    listFile[getPosition()].renameTo(new File(file, "" + editName.getText() + ".jpg"));
}
                       finish();
                       Toast.makeText(DashboardActivity.this, "File Renamed", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                   }
               });
                }
            });
            Button delete=(Button)root.findViewById(R.id.Delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listFile[getPosition()].delete();
                    finish();
                    Toast.makeText(DashboardActivity.this, "File deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }
            });
        }
    }
    public void showImage() {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        Button button = new Button(this);Button button2 = new Button(this);Button button3 = new Button(this);
// button.setText("Share");     button2.setText("Rename");button3.setText("Delete");
      button.setBackgroundResource(android.R.drawable.ic_menu_share); button2.setBackgroundResource(android.R.drawable.ic_menu_edit); button3.setBackgroundResource(android.R.drawable.ic_menu_delete);
//        button.setBackgroundColor(android.graphics.Color.WHITE);button2.setBackgroundColor(android.graphics.Color.WHITE);button3.setBackgroundColor(android.graphics.Color.WHITE);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               shareDocument();
           }
       });  ImageView imageView = new ImageView(this);
        imageView.setImageURI(uri);
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setOrientation(LinearLayout.HORIZONTAL);
  myLayout.setGravity(0);
        myLayout.addView(button);
        myLayout.addView(button2);
        myLayout.addView(button3);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
myLayout.setLayoutParams(params2);
        params1.addRule(RelativeLayout.ABOVE,R.id.imageView);
        layout.addView(imageView, params1);
layout.addView(myLayout);
        builder.setContentView(layout);

        builder.show();

    }
    private void shareDocument() {

        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("application/pdf");
        // Assuming it may go via eMail:
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here is a file from Ocular");
        // Attach the PDf as a Uri, since Android can't take it as bytes yet.
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(mShareIntent, "Share using"));
        return;
    }

}

