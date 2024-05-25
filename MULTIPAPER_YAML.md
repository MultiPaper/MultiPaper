# multipaper.yml

```yaml

# Multi-threading settings
vertical-scaling:

  # The number of threads to use for ticking chunks. `-1` defaults to the number
  # of available processors subtract one, or 1, whichever is larger.
  # Set to `1` to essentially disable multi-threading.
  thread-count: -1

  # The size of a region in chunks. Chunks are grouped into regions when
  # ticking, and neighbouring regions are locked so any changes into the
  # neighbouring region do not step on other threads.
  # The larger the value, the more performant the server will be, but the more
  # likely the work is not evenly distributed between threads. Too small, and
  # threads may accidentally try accessing chunks of another thread.
  # A minimum of 8 chunks is recommended, as lightning rods have the largest
  # search radius at 8 chunks.
  # Must be a power of 2.
  region-size: 8

  # Whether to run plugins' code without 'folia-supported: true' in sync. This
  # will give unsupported plugins a better chance of working, but is not
  # guaranteed. If false, or if the plugin is supported, all the plugin's code
  # will run asynchronously in the ticking region worker threads.
  run-unsupported-plugins-in-sync: true
  
# Multi-server settings
horizontal-scaling:

  # Connection settings for the multipaper-database
  database-connection:
    
    # The address of the multipaper-database server. Set this to something to
    # enable horizontal scaling. Eg `localhost:35353`
    database-address: ""

```