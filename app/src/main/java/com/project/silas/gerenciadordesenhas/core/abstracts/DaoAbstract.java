package com.project.silas.gerenciadordesenhas.core.abstracts;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.business.InicializacaoBusiness;
import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by silas on 04/07/2018.
 */

@SuppressLint("NewApi")
public abstract class DaoAbstract<T> implements AutoCloseable {
    public static final String METADATA_FIELD_TABLENAME = "TABLE_NAME";
    public static final String METADATA_FIELD_ORDERBY = "ORDER_BY";
    public static final String METADATA_CLASSNAME = "Metadata";
    public static final String DEFAULT_FIELD_PK = "id";
    public static final String DEFAULT_GETTER_PK = "getId";


    protected SQLiteDatabase database;
    protected Class<T> modelClass;

    /**
     * Construtor onde é possível injetar um banco de dados para com que o DAO trabalhe.
     * @param sqLiteDatabase
     */
    public DaoAbstract(Class<T> modelClass, SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
        this.modelClass = modelClass;
    }

    /**
     * Construtor padrão. Utilizará o banco de dados obtido pelo singleton do DatabaseBusiness
     */
    public DaoAbstract(Class<T> modelClass) {
        this.database = InicializacaoBusiness.getDatabase();
        this.modelClass = modelClass;
    }

    /**
     * Evitando que a classe seja construída dessa maneira
     */
    private DaoAbstract() {

    }

    /**
     * Método para inserção de entidades. Insere a entidade com os campos que já estão preenchidos
     * @param model
     * @return long id da entidade inserida
     * @throws android.database.sqlite.SQLiteException
     */
    public long insert(T model) {
        String tableName = this.getTableName();
        ContentValues values = this.getContentValues(model, false);
        return this.database.insertOrThrow(tableName, null, values);
    }

    public long insertOrIgnore(T model) {
        return this.insertWithOnConflict(model, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Método para inserção de entidades informando um algoritmo de conflito diferente do padrão.
     * @param model
     * @param conflictAlgorithm
     * @return long id da entidade inserida ou -1 caso o insert tenha fracassado
     */
    public long insertWithOnConflict(T model, int conflictAlgorithm) {
        String tableName = this.getTableName();
        Log.e("Nome", "Nomeda tabela: " + tableName);
        ContentValues values = this.getContentValues(model, false);
        return this.database.insertWithOnConflict(tableName, null, values, conflictAlgorithm);
    }

    /**
     * Método para atualização padrão de entidades.
     * @param model
     * @param conflictAlgorithmId
     * @return int Quantidade de linhas afetadas
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public int updateWithOnConflict(T model, int conflictAlgorithmId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return this.updateWithOnConflict(model, DEFAULT_FIELD_PK + "=?", new String[]{this.getPrimaryKeyValue(model)} , conflictAlgorithmId);
    }

    /**
     * Método para atualização padrão, podendo informar um algoritmo de conflito diferente do padrão.
     * @param model
     * @param whereClause
     * @param whereArgs
     * @param conflictAlgorithmId
     * @return long quantidade de linhas afetadas
     */
    public int updateWithOnConflict(T model, String whereClause, String[] whereArgs, int conflictAlgorithmId) {
        String tableName = this.getTableName();
        ContentValues values = this.getContentValues(model, true);
        return this.database.updateWithOnConflict(tableName, values, whereClause, whereArgs, conflictAlgorithmId);
    }

    /**
     * Método para atualização de entidades. Tentará utilizar o id preenchido na entidade para geração da cláusula where
     * @param model
     * @return int quantidade de linhas afetadas
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public int update(T model) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return this.update(model, DEFAULT_FIELD_PK + "=?", new String[]{ this.getPrimaryKeyValue(model) });
    }

    /**
     * Método para atualização de entidades. Utiliza where e argumentos para selecionar os registros a serem alterados
     * @param model
     * @param whereClause
     * @param whereArgs
     * @return int quantidade de linhas afetadas
     */
    public int update(T model, String whereClause, String[] whereArgs) {
        String tableName = this.getTableName();
        ContentValues values = this.getContentValues(model, true);
        return this.database.update(tableName, values, whereClause, whereArgs);
    }

    /**
     * Apaga um registro do banco. Tenta encontrar qual registro através do id da entidade informada.
     * @param model
     * @return int quantidade de linhas afetadas
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public int delete(T model) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return this.database.delete(this.getTableName(), DEFAULT_FIELD_PK + "=?", new String[]{this.getPrimaryKeyValue(model)});
    }


    /**
     * Método para geração de query
     * @param whereClause
     * @param whereArgs
     * @return Cursor
     */
    public Cursor query(String whereClause, String[] whereArgs) {
        String tableName;
        String orderBy;
        try{
            tableName = this.getTableName();
            orderBy = this.getMetadataValue(METADATA_FIELD_ORDERBY);
        } catch (Exception e) {
            return null;
        }
        return this.database.query(tableName, new String[]{"*"}, whereClause, whereArgs, null, null, orderBy);
    }


    /**
     * Método de criação de query completo com todos os parâmetros
     * @param distinct
     * @param columns
     * @param whereClause
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(boolean distinct, String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) {
        String tableName;
        try{
            tableName = this.getTableName();
        } catch (Exception e) {
            return null;
        }
        if(columns == null) columns = new String[]{"*"};
        Cursor result = this.database.query(distinct, tableName, columns, whereClause, whereArgs, groupBy, having, orderBy, limit);
        if(result != null)
            result.moveToFirst();
        return result;
    }


    private String getMetadataValue(String propertyName) {
        try {
            T model = this.modelClass.newInstance();
            Class[] classes = model.getClass().getDeclaredClasses();
            Field field;
            for (Class classe : classes) {
                if(classe.getName().endsWith(METADATA_CLASSNAME)) {
                    field = classe.getDeclaredField(propertyName);
                    if (field != null) {
                        return (String) field.get(model);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * Retorna o valor da property id do model informado
     * @param model
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String getPrimaryKeyValue(T model) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method idGetter = model.getClass().getMethod(DEFAULT_GETTER_PK);
        return String.valueOf(idGetter.invoke(model));
    }

    /**
     * Retorna o nome da tabela do model informado
     * @return
     */
    private String getTableName(){
        return this.getMetadataValue(METADATA_FIELD_TABLENAME);
    }

    /**
     * Retorna um ContentValues referente a todas as propriedades com getter que a entidade possui, já relacionadas a seus respectivos valores.
     * Útil para uso no insert ou update.
     * @param model
     * @return
     */

    protected ContentValues getContentValues(T model, boolean acceptedNulls) {
        try {
            Method[] methods = model.getClass().getDeclaredMethods();
            ContentValues values = new ContentValues();
            for (Method method : methods) {
                boolean isAnnotationPresent = method.isAnnotationPresent(IgnorePersistence.class);
                if (method.getName().startsWith("get") && !isAnnotationPresent) {
                    String property = method.getName().substring(3);
                    if(method.invoke(model) == null && (!acceptedNulls && property.equals("Id"))) continue;

                    String value = String.valueOf(method.invoke(model));
                    values.put(property, value);
                }
            }
            //Log.e("contentvalues", "valores gerados: " + values.toString());
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        if (database.isOpen()) {
            //database.close();();
        }
    }

    public Cursor rawQuery(String query, @Nullable String[] args) {
        return this.database.rawQuery(query, args);
    }
}
