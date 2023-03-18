package com.org.seratic.lucky.accessData.control;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.org.seratic.lucky.accessData.entities.E_TblMstProvincia;
import com.org.seratic.lucky.accessData.entities.Entity;

public class TblMstProvinciaController extends EntityController{
	
	private SQLiteDatabase db;
	private Cursor dbCursor;
	
	private static final String TABLE = "TBL_MST_PROVINCIA";
	private static final String[] COLUMNS = {"cod_provincia","cod_pais","cod_departamento", "provincia"};
	private static final String ORDER_BY = "TBL_MST_PROVINCIA.provincia ASC";

	
	public TblMstProvinciaController(SQLiteDatabase db) {
		super();
		this.db = db;
	}

	@Override
	public boolean create(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean edit(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Entity> getAll() {
		// TODO Auto-generated method stub
		
		List<Entity> list = new ArrayList<Entity>();
		
		dbCursor = db.query(TABLE, COLUMNS, null, null, null, null, ORDER_BY);
		while(dbCursor.moveToNext()){
			E_TblMstProvincia vo = new E_TblMstProvincia();
			vo.setCod_provincia(dbCursor.getString(0));
			vo.setCod_pais(dbCursor.getString(1));
			vo.setCod_departamento(dbCursor.getString(2));
			vo.setProvincia(dbCursor.getString(3));
			
			list.add(vo);
		}
		
		return list;
	}
	
	public List<Entity> getAll(String cod_pais, String cod_departamento) {
		// TODO Auto-generated method stub
		
		List<Entity> list = new ArrayList<Entity>();
		String selection = "cod_pais = ? and cod_departamento = ?";
		dbCursor = db.query(TABLE, COLUMNS, selection, new String[]{cod_pais, cod_departamento}, null, null, ORDER_BY);
		while(dbCursor.moveToNext()){
			E_TblMstProvincia vo = new E_TblMstProvincia();
			vo.setCod_provincia(dbCursor.getString(0));
			vo.setCod_pais(dbCursor.getString(1));
			vo.setCod_departamento(dbCursor.getString(2));
			vo.setProvincia(dbCursor.getString(3));
			
			list.add(vo);
		}
		
		return list;
	}

}
