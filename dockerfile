FROM ubuntu:20.04

# Install memcached
RUN apt-get update && apt-get install -y memcached

# Expose the memcached port
EXPOSE 11211

# Start memcached
CMD ["memcached"]