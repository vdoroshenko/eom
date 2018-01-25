/**
 * Creates pre-filled demo officemap
 */

print('dump start');

db.officemaps.update(
    {"_id": "demo"},
    {
        "_id": "demo",
        "lastSeen": new Date(),
        "note": "demo map",
    },
    {upsert: true}
);

print('dump complete');