package com.allstudio.askstack;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allstudio.askstack.StackExchange.Filter;
import com.allstudio.askstack.StackExchange.Order;
import com.allstudio.askstack.StackExchange.Question;
import com.allstudio.askstack.StackExchange.QuestionSearchSort;
import com.allstudio.askstack.StackExchange.SearchMethods;
import com.allstudio.askstack.StackExchange.StacManClient;
import com.allstudio.askstack.StackExchange.StacManResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SearchResultActivity extends AppCompatActivity {
    private final String SEARCH_Q = "searchQuery";
    private String searchQuery;
    private TextView t;
    private ImageView errorView;
    private ProgressBar pBar;
    private QuestionAdapter mAdapter;
    private ListView mListView;
    private SharedMemory shared;
    private Button shortingButton, filterButton;

    private ScrollView shortingLayout;
    private RadioButton rs1, rs2, rs3, rs4, ro1, ro2;
    private boolean isOrderAscending = true, isShortLayoutVisible = false;
    private int shortingInt = 4;
    private TextView load;

    private boolean isRetrying = false;
    private int loadedResults = 0, currentPage = 1;
    private Date fromDate = null, toDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        shared = new SharedMemory(this);
        findViewIds();
        setUpListeners();
        handleIntent();
    }

    private void selectRS (int i){
        switch (i) {
            case 1:
                shortingInt = 1;
                rs1.setChecked(true);
                rs2.setChecked(false);
                rs3.setChecked(false);
                rs4.setChecked(false);
                break;
            case 2:
                shortingInt = 2;
                rs1.setChecked(false);
                rs2.setChecked(true);
                rs3.setChecked(false);
                rs4.setChecked(false);
                break;
            case 3:
                shortingInt = 3;
                rs1.setChecked(false);
                rs2.setChecked(false);
                rs3.setChecked(true);
                rs4.setChecked(false);
                break;
            case 4:
                shortingInt = 4;
                rs1.setChecked(false);
                rs2.setChecked(false);
                rs3.setChecked(false);
                rs4.setChecked(true);
                break;
            default:
        }
    }

    private void setUpListeners() {
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                String loading = "Loading...";
                currentPage++;
                t.setText(loading);
                load.setVisibility(View.GONE);
                t.setVisibility(View.VISIBLE);
                checkInternetAndContinue();
            }
        });
        shortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShortLayoutVisible){
                    isShortLayoutVisible = false;
                    shortingLayout.setVisibility(View.GONE);
                    shortingButton.setText("SHORT");
                    mAdapter.clear();
                    loadedResults = 0;
                    pBar.setVisibility(View.VISIBLE);
                    String loading = "Loading...";
                    t.setText(loading);
                    t.setVisibility(View.VISIBLE);
                    load.setVisibility(View.GONE);
                    checkInternetAndContinue();
                } else {
                    isShortLayoutVisible = true;
                    selectRS(shortingInt);
                    shortingLayout.setVisibility(View.VISIBLE);
                    shortingButton.setText("OK");
                }
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchResultActivity.this, FilterActivity.class);
                i.putExtra(SEARCH_Q,searchQuery);
                startActivity(i);
                finish();
            }
        });
        ro1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrderAscending = true;
                ro1.setChecked(true);
                ro2.setChecked(false);
            }
        });
        ro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrderAscending = false;
                ro2.setChecked(true);
                ro1.setChecked(false);
            }
        });
        rs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRS(1);
            }
        });
        rs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRS(2);
            }
        });
        rs3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRS(3);
            }
        });
        rs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRS(4);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
    }

    private void findViewIds() {
        t = findViewById(R.id.sr_t);
        load = findViewById(R.id.sr_loadmore);
        errorView = findViewById(R.id.sr_wifi_error);
        pBar = findViewById(R.id.sr_pbar);
        mListView = findViewById(R.id.sr_list);
        shortingButton = findViewById(R.id.sr_shorting_button);
        filterButton = findViewById(R.id.sr_filter_button);
        shortingLayout = findViewById(R.id.sr_shot_view);
        rs1 = findViewById(R.id.srs_s1);
        rs2 = findViewById(R.id.srs_s2);
        rs3 = findViewById(R.id.srs_s3);
        rs4 = findViewById(R.id.srs_s4);
        ro1 = findViewById(R.id.srs_o1);
        ro2 = findViewById(R.id.srs_o2);

        List<Question> questions = new ArrayList<>();
        mAdapter = new QuestionAdapter(this,R.layout.list_item_sr,questions);
        mListView.setAdapter(mAdapter);
        if(isOrderAscending){
            ro1.setChecked(true);
            ro2.setChecked(false);
        } else {
            ro1.setChecked(false);
            ro2.setChecked(true);
        }
    }

    private void handleIntent() {
        Intent i = getIntent();
        if(i.hasExtra(SEARCH_Q)){
            searchQuery = i.getStringExtra(SEARCH_Q);
            String s = "Searching: " + searchQuery;
            t.setText(s);
            checkInternetAndContinue();
        } else {
            finish();
        }
    }

    public void checkInternetAndContinue() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int timeoutMs = 1500;
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                    sock.connect(sockaddr, timeoutMs);
                    sock.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isRetrying){
                                isRetrying = false;
                                errorView.setVisibility(View.GONE);
                                String loading = "Loading...";
                                t.setText(loading);
                            }
                            startSearchingSimilar();
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retry();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void retry() {
        showErrorMessage();
        new CountDownTimer(2000,2000){
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() { checkInternetAndContinue(); }
        }.start();
    }

    private void showErrorMessage() {
        errorView.setVisibility(View.VISIBLE);
        isRetrying = true;
        String message = "No Internet Connection. Retrying...";
        t.setText(message);
    }

    private void startSearching() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String site = "meta";
                    final String filter = null;
                    final int page = 1;
                    final int pageSize = 5;
                    final Date fromDate = null;
                    final Date toDate = null;
                    final QuestionSearchSort shorting = QuestionSearchSort.Relevance;
                    final Date minDate = null;
                    final Date maxDate = null;
                    final Order order = Order.desc;
                    final String tagged = null;
                    final String notTagged = null;
                    final String inTitle = searchQuery;
                    StacManClient sMC = new StacManClient();
                    SearchMethods sM = new SearchMethods(sMC);
                    final Future<StacManResponse<Question>> queryRelatedQuestions =
                            sM.getMatches(
                                    site,
                                    filter,
                                    page,
                                    pageSize,
                                    fromDate,
                                    toDate,
                                    shorting,
                                    minDate,
                                    maxDate,
                                    null,
                                    null,
                                    order,
                                    tagged,
                                    notTagged,
                                    inTitle);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            setUpList(queryRelatedQuestions);
                        }
                    });
                } catch (final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            t.setText(e.toString());
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void startSearchingAdvance() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String site = "meta";
                    final String filter = null;
                    final int page = 1;
                    final int pageSize = 5;
                    final Date fromDate = null;
                    final Date toDate = null;
                    final QuestionSearchSort shorting = QuestionSearchSort.Relevance;
                    final Date minDate = null;
                    final Date maxDate = null;
                    final Order order = Order.desc;
                    final String tagged = null;
                    final String notTagged = null;
                    final String inTitle = searchQuery;
                    StacManClient sMC = new StacManClient();
                    SearchMethods sM = new SearchMethods(sMC);
                    final Future<StacManResponse<Question>> queryRelatedQuestions =
                            sM.getAdvancedSearchMatches(
                                    site,
                                    filter,
                                    null,
                                    page,
                                    pageSize,
                                    fromDate,
                                    toDate,
                                    shorting,
                                    minDate,
                                    maxDate,
                                    null,
                                    null,
                                    order,
                                    tagged,
                                    notTagged,
                                    inTitle);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            setUpList(queryRelatedQuestions);
                        }
                    });
                } catch (final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            t.setText(e.toString());
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void startSearchingSimilar() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Date minDate = null;
                    final Date maxDate = null;
                    StacManClient sMC = new StacManClient();
                    SearchMethods sM = new SearchMethods(sMC);
                    final Future<StacManResponse<Question>> queryRelatedQuestions =
                            sM.getSimilar(
                                    getSearchSiteName(shared.getSearchingSite()),
                                    shared.getSearchFilter(),
                                    currentPage,
                                    shared.getPageSize(),
                                    fromDate,
                                    toDate,
                                    getShorting(),
                                    minDate,
                                    maxDate,
                                    null,
                                    null,
                                    getOrder(),
                                    shared.getTagged(),
                                    shared.getNotTagged(),
                                    searchQuery);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            t.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            setUpList(queryRelatedQuestions);
                        }
                    });
                } catch (final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pBar.setVisibility(View.GONE);
                            t.setVisibility(View.GONE);
                            t.setText(e.toString());
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private Order getOrder() {
        if(isOrderAscending){
            return Order.asc;
        } else {
            return Order.desc;
        }
    }

    private QuestionSearchSort getShorting() {
        switch (shortingInt) {
            case 1:
                return QuestionSearchSort.Activity;
            case 2:
                return QuestionSearchSort.Votes;
            case 3:
                return QuestionSearchSort.Creation;
            case 4:
                return QuestionSearchSort.Relevance;
            default:
                return  QuestionSearchSort.Relevance;
        }
    }

    private String getSearchSiteName(int searchingSite) {
        switch (searchingSite) {
            case 1:
                return "meta";
            case 2:
                return "stackoverflow";
            case 3:
                return "superuser";
            case 4:
                return "webapps";
            case 5:
                return "webmasters";
            default:
                return "meta";

        }
    }

    private void setUpList(Future<StacManResponse<Question>> queryRelatedQuestions) {
        try {
             List<Question> list = queryRelatedQuestions.get().getData().getItems();
             if(list.size() > 0){
                 for(int i = 0; i < list.size(); i++){
                     mAdapter.add(list.get(i));
                     loadedResults++;
                 }
                 String loaded = loadedResults + " Results Loaded. Click here to Load More.";
                 load.setText(loaded);
                 load.setVisibility(View.VISIBLE);
             } else {
                 String notFound;
                 if(loadedResults > 0){
                     notFound = "No more results found.";
                 } else {
                     mListView.setVisibility(View.GONE);
                     notFound = "We do not found anything related to this query on Stack Exchange. Try to modify your Query and search Again.";
                 }
                 t.setText(notFound);
                 t.setVisibility(View.VISIBLE);
             }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new  Intent(SearchResultActivity.this, MainActivity.class);
        i.putExtra(SEARCH_Q,searchQuery);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    public class QuestionAdapter extends ArrayAdapter<Question> {
        Context ctx;
        QuestionAdapter(Context context, int resource, List<Question> objects) {
            super(context, resource, objects);
            ctx = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item_sr, parent, false);
            }
            LinearLayout root = convertView.findViewById(R.id.li_sr_root);
            ImageView dp = convertView.findViewById(R.id.li_sr_dp);
            TextView title = convertView.findViewById(R.id.li_sr_title);
            TextView username = convertView.findViewById(R.id.li_sr_username);
            TextView cTime = convertView.findViewById(R.id.li_sr_ctime);
            TextView aTime = convertView.findViewById(R.id.li_sr_atime);
            TextView votes = convertView.findViewById(R.id.li_sr_votes);
            ImageView iAns = convertView.findViewById(R.id.li_sr_i_ans);
            TextView ansNum = convertView.findViewById(R.id.li_sr_ans_num);
            TextView views = convertView.findViewById(R.id.li_sr_views);

            final String qDpUrl, qTitle, qUserName, qCTime, qATime, qVotes,qAnsNum, qViews, qURL, qUserURL;
            boolean qIsAnswered;
            Question questionItem = getItem(position);
            if(questionItem != null){
               qDpUrl = questionItem.getOwner().getProfileImage();
               qTitle = questionItem.getTitle();
               qUserName = questionItem.getOwner().getDisplayName();
               qCTime = String.valueOf(questionItem.getCreationDate());
               qATime = String.valueOf(questionItem.getLastActivityDate());
               qVotes = (questionItem.getUpVoteCount() - questionItem.getDownVoteCount()) + " Votes";
               qViews = questionItem.getViewCount() + " Views";
               qAnsNum = questionItem.getAnswerCount() + " Answers";
               qIsAnswered = questionItem.getIsAnswered();
               qURL = questionItem.getLink();
               qUserURL = questionItem.getOwner().getLink();
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.ic_account_circle_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .error(R.drawable.ic_account_circle_black_24dp)
                        .transform(new CenterCrop())
                        .transform(new CircleCrop());
                Glide.with(dp.getContext()).load(qDpUrl).apply(options).into(dp);
                title.setText(qTitle);
                username.setText(qUserName);
                cTime.setText(qCTime);
                aTime.setText(qATime);
                votes.setText(qVotes);
                views.setText(qViews);
                ansNum.setText(qAnsNum);
                if(qIsAnswered) {
                    iAns.setImageResource(R.drawable.ic_comment_green_24dp);
                } else {
                    iAns.setImageResource(R.drawable.ic_comment_black_24dp);
                }
            } else {
                qURL = null;
                qUserURL = null;
                root.setVisibility(View.GONE);
            }

            dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qUserURL));
                    startActivity(browserIntent);
                }
            });

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qURL));
                    startActivity(browserIntent);
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Ask Stack: Question Link", qURL);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ctx, "Question Url is Copied to your Clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            return convertView;
        }

    }

}