/*
 * Copyright (C) 2016 Litote
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.litote.kmongo.coroutine

import com.mongodb.MongoNamespace
import com.mongodb.async.client.ClientSession
import com.mongodb.async.client.ListIndexesIterable
import com.mongodb.async.client.MongoCollection
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.*
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.bson.BsonDocument
import org.bson.conversions.Bson

/**
 * Create a new MongoCollection instance with a different default class to cast any documents returned from the database into..
 *
 * @param <NewTDocument> the default class to cast any documents returned from the database into.
 * @return a new MongoCollection instance with the different default class
 */
@Deprecated("use same function with org.litote.kmongo.async package - will be removed in 4.0")
inline fun <reified NewTDocument : Any> MongoCollection<*>.withDocumentClass(): MongoCollection<NewTDocument> =
    withDocumentClass(NewTDocument::class.java)

/**
 * Counts the number of documents in the collection according to the given options.
 *
 * @param filter   the query filter
 * @param options  the options describing the count
 * @return the callback passed the number of documents in the collection
 * @deprecated use {@link #countDocuments(Bson, CountOptions)} instead
 */
@Deprecated("use countDocuments instead")
suspend fun <T> MongoCollection<T>.count(
    filter: Bson = BsonDocument(),
    options: CountOptions = CountOptions()
): Long = singleResult { count(filter, it) } ?: 0L

/**
 * Counts the number of documents in the collection according to the given options.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param filter   the query filter
 * @param options  the options describing the count
 * @return the callback passed the number of documents in the collection
 * @deprecated use {@link #countDocuments(Bson, CountOptions)} instead
 */
@Deprecated("use countDocuments instead")
suspend fun <T> MongoCollection<T>.count(
    clientSession: ClientSession,
    filter: Bson = BsonDocument(),
    options: CountOptions = CountOptions()
): Long = singleResult { count(clientSession, filter, it) } ?: 0L

/**
 * Counts the number of documents in the collection according to the given options.
 *
 * <p>
 * Note: When migrating from {@code count()} to {@code countDocuments()} the following query operators must be replaced:
 * </p>
 * <pre>
 *
 *  +-------------+--------------------------------+
 *  | Operator    | Replacement                    |
 *  +=============+================================+
 *  | $where      |  $expr                         |
 *  +-------------+--------------------------------+
 *  | $near       |  $geoWithin with $center       |
 *  +-------------+--------------------------------+
 *  | $nearSphere |  $geoWithin with $centerSphere |
 *  +-------------+--------------------------------+
 * </pre>
 *
 * @param filter the query filter
 * @param options the options describing the count
 * @return passed the number of documents in the collection
 */
suspend fun <T> MongoCollection<T>.countDocuments(
    filter: Bson = BsonDocument(),
    options: CountOptions = CountOptions()
): Long = singleResult { countDocuments(filter, options, it) } ?: 0L


/**
 * Counts the number of documents in the collection.
 *
 * <p>
 * Note: When migrating from {@code count()} to {@code countDocuments()} the following query operators must be replaced:
 * </p>
 * <pre>
 *
 *  +-------------+--------------------------------+
 *  | Operator    | Replacement                    |
 *  +=============+================================+
 *  | $where      |  $expr                         |
 *  +-------------+--------------------------------+
 *  | $near       |  $geoWithin with $center       |
 *  +-------------+--------------------------------+
 *  | $nearSphere |  $geoWithin with $centerSphere |
 *  +-------------+--------------------------------+
 * </pre>
 *
 * @param clientSession the client session with which to associate this operation
 * @return the number of documents in the collection
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.countDocuments(
    clientSession: ClientSession,
    filter: Bson = BsonDocument(),
    options: CountOptions = CountOptions()
): Long = singleResult { countDocuments(clientSession, filter, options, it) } ?: 0L

/**
 * Gets an estimate of the count of documents in a collection using collection metadata.
 *
 * @param options the options describing the count
 * @return the number of documents in the collection
 */
suspend fun <T> MongoCollection<T>.estimatedDocumentCount(
    options: EstimatedDocumentCountOptions = EstimatedDocumentCountOptions()
): Long = singleResult { estimatedDocumentCount(options, it) } ?: 0L


/**
 * Executes a mix of inserts, updates, replaces, and deletes.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.
 * The eligibility for retryable write support for bulk operations is determined on the whole bulk write. If the {@code requests}
 * contain any {@code UpdateManyModels} or {@code DeleteManyModels} then the bulk operation will not support retryable writes.</p>
 * @param requests the writes to execute
 * @param options  the options to apply to the bulk write operation
 * @return the result of the bulk write
 */
suspend inline fun <reified T : Any> MongoCollection<T>.bulkWrite(
    requests: List<WriteModel<T>>,
    options: BulkWriteOptions = BulkWriteOptions()
): BulkWriteResult? {
    return singleResult { bulkWrite(requests, options, it) }
}

/**
 * Executes a mix of inserts, updates, replaces, and deletes.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.
 * The eligibility for retryable write support for bulk operations is determined on the whole bulk write. If the {@code requests}
 * contain any {@code UpdateManyModels} or {@code DeleteManyModels} then the bulk operation will not support retryable writes.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param requests the writes to execute
 * @return the result of the bulk write
 * @mongodb.server.release 3.6
 */
suspend inline fun <reified T : Any> MongoCollection<T>.bulkWrite(
    clientSession: ClientSession,
    vararg requests: WriteModel<T>,
    options: BulkWriteOptions = BulkWriteOptions()
): BulkWriteResult? = singleResult { bulkWrite(clientSession, requests.toList(), options, it) }


/**
 * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param document the document to insert
 * @param options  the options to apply to the operation
 *
 * @return that is completed once the insert has completed
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoCommandException      returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 */
suspend fun <TDocument : Any> MongoCollection<TDocument>.insertOne(
    document: TDocument,
    options: InsertOneOptions = InsertOneOptions()
): Void? = singleResult { insertOne(document, options, it) }


/**
 * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param document the document to insert
 * @return that is completed once the insert has completed
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.server.release 3.6
 */
suspend fun <TDocument : Any> MongoCollection<TDocument>.insertOne(
    clientSession: ClientSession,
    document: TDocument,
    options: InsertOneOptions = InsertOneOptions()
): Void? = singleResult { insertOne(clientSession, document, options, it) }


/**
 * Inserts one or more documents.  A call to this method is equivalent to a call to the {@code bulkWrite} method
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param documents the documents to insert
 * @return that is completed once the insert has completed
 * @throws com.mongodb.MongoBulkWriteException if there's an exception in the bulk write operation
 * @throws com.mongodb.MongoException          if the write failed due some other failure
 * @see com.mongodb.async.client.MongoCollection#bulkWrite
 */
suspend fun <T : Any> MongoCollection<T>.insertMany(
    documents: List<T>,
    options: InsertManyOptions = InsertManyOptions()
): Void? = singleResult { insertMany(documents, options, it) }

/**
 * Inserts one or more documents.  A call to this method is equivalent to a call to the {@code bulkWrite} method
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param documents the documents to insert
 * @return the callback that is completed once the insert has completed
 * @throws com.mongodb.MongoBulkWriteException if there's an exception in the bulk write operation
 * @throws com.mongodb.MongoException          if the write failed due some other failure
 * @see com.mongodb.async.client.MongoCollection#bulkWrite
 * @mongodb.server.release 3.6
 */
suspend fun <T : Any> MongoCollection<T>.insertMany(
    clientSession: ClientSession,
    documents: List<T>,
    options: InsertManyOptions = InsertManyOptions()
): Void? = singleResult { insertMany(clientSession, documents, options, it) }


/**
 * Removes at most one document from the collection that matches the given filter.  If no documents match, the collection is not
 * modified.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param filter   the query filter to apply the the delete operation
 * @param options  the options to apply to the delete operation
 * @return the result of the remove one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 */
suspend fun <T> MongoCollection<T>.deleteOne(
    filter: Bson,
    options: DeleteOptions = DeleteOptions()
): DeleteResult? = singleResult { deleteOne(filter, options, it) }

/**
 * Removes at most one document from the collection that matches the given filter.  If no documents match, the collection is not
 * modified.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter   the query filter to apply the the delete operation
 * @return the result of the remove one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.deleteOne(
    clientSession: ClientSession,
    filter: Bson,
    options: DeleteOptions = DeleteOptions()
): DeleteResult? = singleResult { deleteOne(clientSession, filter, options, it) }


/**
 * Removes all documents from the collection that match the given query filter.  If no documents match, the collection is not modified.
 *
 * @param filter   the query filter to apply the the delete operation
 * @param options  the options to apply to the delete operation
 * @return the result of the remove many operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 */
suspend fun <T> MongoCollection<T>.deleteMany(
    filter: Bson,
    options: DeleteOptions = DeleteOptions()
): DeleteResult? = singleResult { deleteMany(filter, options, it) }


/**
 * Removes all documents from the collection that match the given query filter.  If no documents match, the collection is not modified.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param filter   the query filter to apply the the delete operation
 * @return the result of the remove many operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @since 3.6
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.deleteMany(
    clientSession: ClientSession,
    filter: Bson,
    options: DeleteOptions = DeleteOptions()
): DeleteResult? = singleResult { deleteMany(clientSession, filter, options, it) }


/**
 * Replace a document in the collection according to the specified arguments.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param filter      the query filter to apply the the replace operation
 * @param replacement the replacement document
 * @param options     the options to apply to the replace operation
 * @return  the result of the replace one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
 */
suspend fun <T : Any> MongoCollection<T>.replaceOne(
    filter: Bson,
    replacement: T,
    options: ReplaceOptions = ReplaceOptions()
): UpdateResult? = singleResult { replaceOne(filter, replacement, options, it) }

/**
 * Replace a document in the collection according to the specified arguments.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter      the query filter to apply the the replace operation
 * @param replacement the replacement document
 * @return the result of the replace one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
 * @mongodb.server.release 3.6
 */
suspend fun <T : Any> MongoCollection<T>.replaceOne(
    clientSession: ClientSession,
    filter: Bson,
    replacement: T,
    options: ReplaceOptions = ReplaceOptions()
): UpdateResult? = singleResult { replaceOne(clientSession, filter, replacement, options, it) }


/**
 * Update a single document in the collection according to the specified arguments.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators.
 * @param options  the options to apply to the update operation
 * @return the result of the update one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/ Updates
 * @mongodb.driver.manual reference/operator/update/ Update Operators
 */
suspend inline fun <reified T : Any> MongoCollection<T>.updateOne(
    filter: Bson,
    update: Bson,
    options: UpdateOptions = UpdateOptions()
): UpdateResult? = singleResult { updateOne(filter, update, options, it) }


/**
 * Update a single document in the collection according to the specified arguments.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators.
 * @return the result of the update one operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/ Updates
 * @mongodb.driver.manual reference/operator/update/ Update Operators
 * @since 3.6
 * @mongodb.server.release 3.6
 */
suspend inline fun <reified T : Any> MongoCollection<T>.updateOne(
    clientSession: ClientSession,
    filter: Bson,
    update: Bson,
    options: UpdateOptions = UpdateOptions()
): UpdateResult? = singleResult { updateOne(clientSession, filter, update, options, it) }

/**
 * Update all documents in the collection according to the specified arguments.
 *
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators.
 * @param options  the options to apply to the update operation
 * @return the result of the update many operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/ Updates
 * @mongodb.driver.manual reference/operator/update/ Update Operators
 */
suspend fun <T> MongoCollection<T>.updateMany(
    filter: Bson,
    update: Bson,
    options: UpdateOptions = UpdateOptions()
): UpdateResult? = singleResult { updateMany(filter, update, options, it) }

/**
 * Update all documents in the collection according to the specified arguments.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators. T
 * @return the result of the update many operation
 * @throws com.mongodb.MongoWriteException        returned via the callback
 * @throws com.mongodb.MongoWriteConcernException returned via the callback
 * @throws com.mongodb.MongoException             returned via the callback
 * @mongodb.driver.manual tutorial/modify-documents/ Updates
 * @mongodb.driver.manual reference/operator/update/ Update Operators
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.updateMany(
    clientSession: ClientSession,
    filter: Bson,
    update: Bson,
    options: UpdateOptions = UpdateOptions()
): UpdateResult? = singleResult { updateMany(clientSession, filter, update, options, it) }


/**
 * Atomically find a document and remove it.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param filter   the query filter to find the document with
 * @param options  the options to apply to the operation
 * @return the document that was removed.  If no documents matched the query filter, then null will be returned
 */
suspend fun <T> MongoCollection<T>.findOneAndDelete(
    filter: Bson,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions()
): T? = singleResult { findOneAndDelete(filter, options, it) }

/**
 * Atomically find a document and remove it.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter   the query filter to find the document with
 * @param options  the options to apply to the operation
 * @return the document that was removed.  If no documents matched the query filter, then null will be returned
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.findOneAndDelete(
    clientSession: ClientSession,
    filter: Bson,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions()
): T? = singleResult { findOneAndDelete(clientSession, filter, options, it) }

/**
 * Atomically find a document and replace it.
 *
 * @param filter      the query filter to apply the the replace operation
 * @param replacement the replacement document
 * @param options     the options to apply to the operation
 * @return the document that was replaced.  Depending on the value of the `returnOriginal` property, this will either be the
 * document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
 * returned
 */
suspend fun <T> MongoCollection<T>.findOneAndReplace(
    filter: Bson,
    replacement: T,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions()
): T? = singleResult { findOneAndReplace(filter, replacement, options, it) }

/**
 * Atomically find a document and replace it.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter      the query filter to apply the the replace operation
 * @param replacement the replacement document
 * @param options     the options to apply to the operation
 * @return the document that was replaced.  Depending on the value of the `returnOriginal` property, this will either be the
 * document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
 * returned
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.findOneAndReplace(
    clientSession: ClientSession,
    filter: Bson,
    replacement: T,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions()
): T? = singleResult { findOneAndReplace(clientSession, filter, replacement, options, it) }


/**
 * Atomically find a document and update it.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators.
 * @param options  the options to apply to the operation
 * @return the document that was updated.  Depending on the value of the {@code returnOriginal} property, this will either be
 * the document as it was before the update or as it is after the update.  If no documents matched the query filter, then null will be
 * returned
 */
suspend fun <T> MongoCollection<T>.findOneAndUpdate(
    filter: Bson,
    update: Bson,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): T? = singleResult { findOneAndUpdate(filter, update, options, it) }

/**
 * Atomically find a document and update it.
 *
 * <p>Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.</p>
 * @param clientSession  the client session with which to associate this operation
 * @param filter   a document describing the query filter, which may not be null.
 * @param update   a document describing the update, which may not be null. The update to apply must include only update operators.
 * @param options  the options to apply to the operation
 * @return the document that was updated before the update was applied.  If no documents matched the query filter, then null will be
 * returned
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.findOneAndUpdate(
    session: ClientSession,
    filter: Bson,
    update: Bson,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): T? = singleResult { findOneAndUpdate(session, filter, update, options, it) }

/**
 * Drops this collection from the Database.
 *
 * @return that is completed once the collection has been dropped
 * @mongodb.driver.manual reference/command/drop/ Drop Collection
 */
suspend fun <T> MongoCollection<T>.drop(): Void? =
    singleResult { drop(it) }

/**
 * Drops this collection from the Database.
 *
 * @param clientSession  the client session with which to associate this operation
 * @return that is completed once the collection has been dropped
 * @mongodb.driver.manual reference/command/drop/ Drop Collection
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.drop(clientSession: ClientSession): Void? =
    singleResult { drop(clientSession, it) }

/**
 * Creates an index.  If successful, the callback will be executed with the name of the created index as the result.
 *
 * @param key      an object describing the index key(s), which may not be null.
 * @return that is completed once the index has been created
 * @mongodb.driver.manual reference/command/createIndexes/ Create indexes
 */
suspend fun <T> MongoCollection<T>.createIndex(
    key: Bson,
    options: IndexOptions = IndexOptions()
): String? = singleResult { createIndex(key, options, it) }

/**
 * Creates an index.  If successful, the callback will be executed with the name of the created index as the result.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param key      an object describing the index key(s), which may not be null.
 * @return that is completed once the index has been created
 * @mongodb.driver.manual reference/command/createIndexes/ Create indexes
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.createIndex(
    clientSession: ClientSession,
    key: Bson,
    options: IndexOptions = IndexOptions()
): String? = singleResult { createIndex(clientSession, key, options, it) }

/**
 * Create multiple indexes. If successful, the callback will be executed with a list of the names of the created indexes as the result.
 *
 * @param indexes the list of indexes
 * @param createIndexOptions options to use when creating indexes
 * @return that is completed once the indexes has been created
 * @mongodb.driver.manual reference/command/createIndexes Create indexes
 */
suspend fun <T> MongoCollection<T>.createIndexes(
    indexes: List<IndexModel>,
    createIndexOptions: CreateIndexOptions = CreateIndexOptions()
): List<String>? = singleResult { createIndexes(indexes, createIndexOptions, it) }

/**
 * Create multiple indexes. If successful, the callback will be executed with a list of the names of the created indexes as the result.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param indexes the list of indexes
 * @param createIndexOptions options to use when creating indexes
 * @return that is completed once the indexes has been created
 * @mongodb.driver.manual reference/command/createIndexes Create indexes
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.createIndexes(
    clientSession: ClientSession,
    indexes: List<IndexModel>,
    createIndexOptions: CreateIndexOptions = CreateIndexOptions()
): List<String>? = singleResult { createIndexes(indexes, createIndexOptions, it) }


/**
 * Get all the indexes in this collection.
 *
 * @param <TResult>   the target document type of the iterable.
 * @return the list indexes iterable interface
 */
inline fun <reified TResult : Any> MongoCollection<*>.listTypedIndexes(): ListIndexesIterable<TResult> =
    listIndexes(TResult::class.java)

/**
 * Drops the index given its name.
 *
 * @param indexName the name of the index to remove
 * @return that is completed once the index has been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 */
suspend fun <T> MongoCollection<T>.dropIndex(
    indexName: String,
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndex(indexName, dropIndexOptions, it) }

/**
 * Drops the index given the keys used to create it.
 *
 * @param keys the keys of the index to remove
 * @param dropIndexOptions options to use when dropping indexes
 * @return that is completed once the index has been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 * @since 3.6
 */
suspend fun <T> MongoCollection<T>.dropIndex(
    keys: Bson,
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndex(keys, dropIndexOptions, it) }

/**
 * Drops the index given its name.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param indexName the name of the index to remove
 * @return that is completed once the index has been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 */
suspend fun <T> MongoCollection<T>.dropIndex(
    clientSession: ClientSession,
    indexName: String,
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndex(clientSession, indexName, dropIndexOptions, it) }


/**
 * Drops the index given the keys used to create it.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param keys the keys of the index to remove
 * @param dropIndexOptions options to use when dropping indexes
 * @return that is completed once the index has been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 */
suspend fun <T> MongoCollection<T>.dropIndex(
    clientSession: ClientSession,
    keys: Bson,
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndex(clientSession, keys, dropIndexOptions, it) }

/**
 * Drop all the indexes on this collection, except for the default on _id.
 *
 * @param dropIndexOptions options to use when dropping indexes
 * @return that is completed once all the indexes have been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 */
suspend fun <T> MongoCollection<T>.dropIndexes(
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndexes(dropIndexOptions, it) }


/**
 * Drop all the indexes on this collection, except for the default on _id.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param dropIndexOptions options to use when dropping indexes
 * @return that is completed once all the indexes have been dropped
 * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
 */
suspend fun <T> MongoCollection<T>.dropIndexes(
    clientSession: ClientSession,
    dropIndexOptions: DropIndexOptions = DropIndexOptions()
): Void? = singleResult { dropIndexes(clientSession, dropIndexOptions, it) }


/**
 * Rename the collection with oldCollectionName to the newCollectionName.
 *
 * @param newCollectionNamespace the name the collection will be renamed to
 * @param options                the options for renaming a collection
 * @return that is completed once the collection has been renamed
 * @throws com.mongodb.MongoServerException if you provide a newCollectionName that is the name of an existing collection and dropTarget
 *                                          is false, or if the oldCollectionName is the name of a collection that doesn't exist
 * @mongodb.driver.manual reference/command/renameCollection Rename collection
 */
suspend fun <T> MongoCollection<T>.renameCollection(
    newCollectionNamespace: MongoNamespace,
    options: RenameCollectionOptions = RenameCollectionOptions()
): Void? = singleResult { renameCollection(newCollectionNamespace, options, it) }


/**
 * Rename the collection with oldCollectionName to the newCollectionName.
 *
 * @param clientSession  the client session with which to associate this operation
 * @param newCollectionNamespace the name the collection will be renamed to
 * @param options                the options for renaming a collection
 * @return that is completed once the collection has been renamed
 * @throws com.mongodb.MongoServerException if you provide a newCollectionName that is the name of an existing collection and dropTarget
 *                                          is false, or if the oldCollectionName is the name of a collection that doesn't exist
 * @mongodb.driver.manual reference/command/renameCollection Rename collection
 * @since 3.6
 * @mongodb.server.release 3.6
 */
suspend fun <T> MongoCollection<T>.renameCollection(
    clientSession: ClientSession,
    newCollectionNamespace: MongoNamespace,
    options: RenameCollectionOptions = RenameCollectionOptions()
): Void? = singleResult { renameCollection(clientSession, newCollectionNamespace, options, it) }
