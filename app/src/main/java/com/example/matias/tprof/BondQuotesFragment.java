package com.example.matias.tprof;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.sync.AppSyncAdapter;


public class BondQuotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BONDS_LOADER = 1;

    private static final String[] BOND_QUOTE_COLUMNS = {
            QuotesContract.BondEntry.TABLE_NAME + "." + QuotesContract.BondEntry._ID,
            QuotesContract.BondEntry.COLUMN_FULLNAME,
            QuotesContract.BondEntry.COLUMN_SYMBOL,
            QuotesContract.BondQuotesEntry.TABLE_NAME + "." + QuotesContract.BondQuotesEntry._ID,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_PRICE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_TRADE_DATE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE,
            QuotesContract.BondQuotesEntry.COLUMN_LAST_CHANGE_PERCENTAGE,
            QuotesContract.BondQuotesEntry.COLUMN_CURRENCY,
    };

    static final int COL_BOND_ID = 0;
    static final int COL_FULLNAME = 1;
    static final int COL_SYMBOL = 2;
    static final int COL_BOND_QUOTE_ID = 3;
    static final int COL_LAST_PRICE = 4;
    static final int COL_LAST_TRADE_DATE = 5;
    static final int COL_LAST_CHANGE = 6;
    static final int COL_LAST_CHANGE_PERCENTAGE = 7;
    static final int COL_CURRENCY = 8;

    private BondQuotesAdapter mQuotesAdapter;

    public BondQuotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String sortOrder = QuotesContract.BondEntry.COLUMN_FULLNAME + " ASC";
        Uri bondsUri = QuotesContract.BondQuotesEntry.CONTENT_URI;

        Cursor cur = getActivity().getContentResolver().query(bondsUri,
                null, null, null, sortOrder);

        mQuotesAdapter = new BondQuotesAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_bond_quotes, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_bond_quotes);
        listView.setAdapter(mQuotesAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BONDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateQuotes();
    }

    private void updateQuotes() {
        AppSyncAdapter.syncImmediately(getActivity());
       // FetchBondDataTask fetchTestDataTask = new FetchBondDataTask(getContext());
        //fetchTestDataTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = QuotesContract.BondEntry.COLUMN_FULLNAME + " ASC";
        Uri stocksUri = QuotesContract.BondQuotesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                stocksUri,
                BOND_QUOTE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mQuotesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuotesAdapter.swapCursor(null);
    }

}