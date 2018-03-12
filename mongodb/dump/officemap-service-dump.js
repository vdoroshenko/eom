/**
 * Creates pre-filled demo officemap
 */

print('dump start');

db.officemaps.update(
    {"_id": "vinnytsia.ua"},
    {
        "_id": "vinnytsia.ua",
        "lastSeen": new Date(),
        "note": "Vinnytsia office map",
        "employeeLocationList": [
            {
                "uid": "mabouemish",
                "floor": 1,
                "room": "1",
                "seat": "102"
            },
            {
                "uid": "gabramovych",
                "floor": 1,
                "room": "1",
                "seat": "103"
            },
            {
                "uid": "iandriienko",
                "floor": 1,
                "room": "1",
                "seat": "104"
            },
            {
                "uid": "aartyukh",
                "floor": 1,
                "room": "1",
                "seat": "105"
            },
            {
                "uid": "abaev",
                "floor": 1,
                "room": "1",
                "seat": "106"
            },
            {
                "uid": "aberzin",
                "floor": 1,
                "room": "1",
                "seat": "107"
            },
            {
                "uid": "ybilous",
                "floor": 1,
                "room": "1",
                "seat": "108"
            },
            {
                "uid": "abondar",
                "floor": 1,
                "room": "1",
                "seat": "109"
            },
            {
                "uid": "acebotari",
                "floor": 1,
                "room": "1",
                "seat": "110"
            },
            {
                "uid": "ycherniavskyi",
                "floor": 1,
                "room": "1",
                "seat": "111"
            }
        ]
    },
    {upsert: true}
);

print('dump complete');