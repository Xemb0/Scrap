package com.launcher.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private RecyclerView recyclerView;
    private List<ApplicationInfo> applicationInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);
        applicationInfoList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AppAdapter(this, applicationInfoList));

        updateAppList();
    }

    @SuppressLint("SetTextI18n")
    private void updateAppList() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        int numApps = 0;
        for (ApplicationInfo appInfo : allApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                applicationInfoList.add(appInfo);
                numApps++;
            }
        }
        textView.setText(numApps + " Apps are installed");
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private static class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
        private final Context context;
        private final List<ApplicationInfo> applicationInfoList;
        private final PackageManager packageManager;

        public AppAdapter(Context context, List<ApplicationInfo> applicationInfoList) {
            this.context = context;
            this.applicationInfoList = applicationInfoList;
            this.packageManager = context.getPackageManager();
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
            return new AppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppViewHolder holder, int position) {
            ApplicationInfo appInfo = applicationInfoList.get(position);
            CharSequence appName = packageManager.getApplicationLabel(appInfo);
            holder.textView.setText(appName != null ? appName.toString() : appInfo.packageName);
            holder.imageView.setImageDrawable(packageManager.getApplicationIcon(appInfo));
            holder.itemView.setOnClickListener(view -> {
                String packageName = applicationInfoList.get(position).packageName;
                Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return applicationInfoList.size();
        }

        public static class AppViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public AppViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.app_icon);
                textView = itemView.findViewById(R.id.app_name);
            }
        }
    }
}
