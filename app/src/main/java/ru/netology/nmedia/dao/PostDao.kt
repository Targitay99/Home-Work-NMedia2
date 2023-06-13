package ru.netology.nmedia.dao


import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity  ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity  ORDER BY id DESC")
    fun getPagingSourse(): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
    UPDATE PostEntity SET
    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
    WHERE id = :id
    """)
    suspend fun likeById(id: Long)

    @Query("""
    UPDATE PostEntity SET
    hidden = 0
    WHERE hidden = 1
    """)
    suspend fun viewNewPost()

    @Query("DELETE FROM PostEntity")
    suspend fun clear()
}

//class Converters {
//    @TypeConverter
//    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
//    @TypeConverter
//    fun fromAttachmentType(value: AttachmentType) = value.name
//}
